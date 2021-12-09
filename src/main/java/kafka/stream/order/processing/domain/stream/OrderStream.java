package kafka.stream.order.processing.domain.stream;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import kafka.stream.order.processing.domain.joiner.OrderProductJoiner;
import kafka.stream.order.processing.domain.joiner.ProductPriceJoiner;
import kafka.stream.order.processing.domain.model.DetailedOrder;
import kafka.stream.order.processing.domain.model.IndividualOrderItem;
import kafka.stream.order.processing.domain.model.Order;
import kafka.stream.order.processing.domain.model.PricedOrderItem;
import kafka.stream.order.processing.domain.model.PricedProduct;
import kafka.stream.order.processing.domain.model.Product;
import kafka.stream.order.processing.domain.model.ProductPriceEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Merger;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.Windowed;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.inject.Singleton;

@Factory
@Slf4j
public class OrderStream {

    public static final String ORDERS_INPUT = "orders";
    public static final String PRICES_INPUT = "prices";
    public static final String PRODUCTS_INPUT = "products";

    @Singleton
    KStream<String, DetailedOrder> orderStream(ConfiguredStreamBuilder builder) {

        Properties props = builder.getConfiguration();
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KStream<String, Order> orderSource = builder.stream(ORDERS_INPUT);

        KTable<String, Product> products = builder.table(PRODUCTS_INPUT);
        KTable<String, ProductPriceEvent> prices = builder.table(PRICES_INPUT);
        KTable<String, PricedProduct> pricedProducts = products.leftJoin(prices, new ProductPriceJoiner());

        prices.toStream().foreach((key, value) -> log.info("New price {} {}", key, value));

        pricedProducts.toStream().foreach((key, value) -> log.info("Priced Product {} {}", key, value));

        orderSource.foreach((key, value) -> log.info("Received order {} with items {}", value.getId(), (long) value.getItems().size()));

        KStream<String, IndividualOrderItem> orderItemSource =  orderSource.flatMap(mapToOrderItems());

        orderItemSource.foreach((key, value) -> log.info("Item in list {} {}", key, value));

        KStream<String, PricedOrderItem> pricedOrderItemSource = orderItemSource.join(pricedProducts, new OrderProductJoiner());

        pricedOrderItemSource.foreach((key, value) -> log.info("Priced items {} {}", key, value));



        KStream<String, DetailedOrder> detailedOrderSource =
                pricedOrderItemSource.map((productId, pricedOrderItem) -> KeyValue.pair(pricedOrderItem.getOrderId(), pricedOrderItem))
                        .groupByKey()
                        .windowedBy(SessionWindows.with(Duration.ofMinutes(5)).grace(Duration.ofSeconds(30)))
                        .aggregate(DetailedOrder::new, aggregateSpans(), joinAggregates())
                        .toStream()
                        .map(mapToOrder());

        detailedOrderSource.foreach((key, order) -> log.info("New order {}, contents {}, total {}", key, order.getItems(), order.getTotalCost()));

        return detailedOrderSource;
    }

    Merger<String, DetailedOrder> joinAggregates() {
        return (aggKey, aggOne, aggTwo) -> {
            List<PricedOrderItem> items = aggOne.getItems();
            if (items != null) { aggTwo.getItems().addAll(items); }
            return aggTwo;
        };
    }

    Aggregator<String, PricedOrderItem, DetailedOrder> aggregateSpans() {
        return (id, pricedOrderItem, order) -> {
            order.setId(pricedOrderItem.getId());
            if (order.getItems() == null) { order.setItems(new ArrayList<>()); }
            order.getItems().add(pricedOrderItem);
            return order;
        };
    }

    KeyValueMapper<Windowed<String>, DetailedOrder, KeyValue<String, DetailedOrder>> mapToOrder() {
        return (id, order) -> {
            long cost = order.getItems().stream().reduce(0L, (subtotal, item) -> subtotal + getOrderItemTotalPrice(item), Long::sum);
            order.setTotalCost(cost);
            return KeyValue.pair(id.key(), order);
        };
    }

    KeyValueMapper<String, Order, List<KeyValue<String, IndividualOrderItem>>> mapToOrderItems() {
        return (key, order) -> order.getItems().stream()
                .map(orderItem -> KeyValue.pair(orderItem.getProductId(),
                        IndividualOrderItem.newBuilder().setOrderId(key).setProductId(orderItem.getProductId()).setQuantity(orderItem.getQuantity()).build()))
                .collect(Collectors.toList());
    }

    long getOrderItemTotalPrice(PricedOrderItem item) {
        return item.getPrice() * item.getQuantity();
    }

}
