package kafka.stream.order.processing.domain.stream;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import kafka.stream.order.processing.domain.joiner.OrderProductJoiner;
import kafka.stream.order.processing.domain.joiner.ProductPriceJoiner;
import kafka.stream.order.processing.domain.model.DetailedOrder;
import kafka.stream.order.processing.domain.model.IndividualOrderItem;
import kafka.stream.order.processing.domain.model.Order;
import kafka.stream.order.processing.domain.model.OrderItem;
import kafka.stream.order.processing.domain.model.PricedOrderItem;
import kafka.stream.order.processing.domain.model.PricedProduct;
import kafka.stream.order.processing.domain.model.Product;
import kafka.stream.order.processing.domain.model.ProductPriceEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.Windows;

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

        KStream<String, IndividualOrderItem> orderItemSource = orderSource.flatMap((key, order) -> order.getItems().stream()
                .map(orderItem -> KeyValue.pair(orderItem.getProductId(),
                        IndividualOrderItem.builder().orderId(key).productId(orderItem.getProductId()).quantity(orderItem.getQuantity()).build()))
                .collect(Collectors.toList()));

        KStream<String, PricedOrderItem> pricedOrderItemSource = orderItemSource.join(pricedProducts, new OrderProductJoiner());

        KStream<Windowed<String>, List<PricedOrderItem>> detailedOrderSource =
                pricedOrderItemSource.map((productId, pricedOrderItem) -> KeyValue.pair(pricedOrderItem.getOrderId(), pricedOrderItem))
                        .groupByKey()
                        .windowedBy(SessionWindows.with(Duration.ofMinutes(10)))
                        .aggregate(ArrayList::new, (key, value, aggregate) -> {
                            aggregate.add(value);
                            return aggregate;
                        }, Materialized.as("OrderProductList")).toStream()
                        ;

        detailedOrderSource.foreach((key, order) -> log.info("New order {}, contents {}, total {}", key, order.getItems(), order.getTotalCost()));

        return detailedOrderSource;

    }

}
