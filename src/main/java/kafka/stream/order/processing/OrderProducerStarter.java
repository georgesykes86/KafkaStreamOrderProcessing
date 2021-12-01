package kafka.stream.order.processing;

import static java.lang.Thread.sleep;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import kafka.stream.order.processing.domain.model.Order;
import kafka.stream.order.processing.domain.model.OrderItem;
import kafka.stream.order.processing.domain.producer.OrderClient;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class OrderProducerStarter {

    private OrderClient orderClient;

    @Inject
    public OrderProducerStarter(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @EventListener
    public void startProducingOrders(final StartupEvent event) throws InterruptedException {
        log.info("Starting the producer");

        while(true) {
            sleep(1000);
            UUID orderId = UUID.randomUUID();
            orderClient.sendOrder(orderId, createOrder(orderId));
        }
    }

    private Order createOrder(UUID orderId) {
        return Order.newBuilder()
                .setId(orderId.toString())
                .setItems(IntStream.range(0, 2)
                        .mapToObj(num -> new OrderItem(UUID.randomUUID().toString(), 2))
                        .collect(Collectors.toList()))
                .build();
    }

}
