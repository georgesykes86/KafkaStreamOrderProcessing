package kafka.stream.order.processing.domain.producer;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import kafka.stream.order.processing.domain.model.ProductPriceEvent;

@KafkaClient(id = "price-client")
public interface PriceClient {

    @Topic("prices")
    void sendPrice(@KafkaKey String key, ProductPriceEvent productPrice);

}
