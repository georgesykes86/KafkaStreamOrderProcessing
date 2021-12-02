package kafka.stream.order.processing.domain.producer;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import kafka.stream.order.processing.domain.model.Product;

import java.util.UUID;

@KafkaClient(id = "product-client")
public interface ProductClient {

    @Topic("products")
    void sendProduct(@KafkaKey UUID key, Product product);

}
