package kafka.stream.order.processing.domain.producer;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import kafka.stream.order.processing.domain.model.Product;

@KafkaClient(id = "product-client")
public interface ProductClient {

    @Topic("products")
    void sendProduct(@KafkaKey String key, Product product);

}
