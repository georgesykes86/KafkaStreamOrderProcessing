package kafka.stream.order.processing.domain.producer;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import kafka.stream.order.processing.domain.model.Order;

import java.util.UUID;

@KafkaClient(id = "order-client")
public interface OrderClient {

    @Topic("orders")
    void sendOrder(@KafkaKey UUID key, Order order);

}
