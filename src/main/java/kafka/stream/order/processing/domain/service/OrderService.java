package kafka.stream.order.processing.domain.service;

import io.micronaut.context.annotation.Factory;
import kafka.stream.order.processing.domain.model.Order;
import kafka.stream.order.processing.domain.model.PricedOrderItem;
import kafka.stream.order.processing.domain.model.Product;
import kafka.stream.order.processing.domain.model.ProductPriceEvent;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Factory
public class OrderService {

    private KStream<UUID, Order> orderStream;
    private final KStream<UUID, ProductPriceEvent> priceStream;
    private KStream<UUID, Product> productStream;

    @Inject
    public OrderService(@Named("order-stream") KStream<UUID, Order> orderStream, @Named("price-stream") KStream<UUID, ProductPriceEvent> priceStream,
            @Named("product-stream") KStream<UUID, Product> productStream){
        this.orderStream = orderStream;
        this.priceStream = priceStream;
        this.productStream = productStream;
    }

    @Singleton
    void populateOrderDetails() {
        KTable<String, Long> productPrices =
                priceStream.map((key, value) -> KeyValue.pair(value.getProductId(), value.getPrice())).toTable(Materialized.as("price-store"));

        KTable<String, String> productNames =
                productStream.map(((key, value) -> KeyValue.pair(value.getId(), value.getName()))).toTable(Materialized.as("product-store"));

        orderStream.foreach((uid, order) -> {
            order.getItems().stream()
                    .map(item -> PricedOrderItem.builder()
                            .id(item.getProductId())
                            .quantity(item.getQuantity())
                            .name("X")
                            .quantity(1)
                            .build());
        });
    }



}
