package kafka.stream.order.processing.domain.stream;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import kafka.stream.order.processing.domain.model.ProductPriceEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;
import java.util.UUID;
import javax.inject.Named;
import javax.inject.Singleton;

@Factory
@Slf4j
public class PriceStream {

    public static final String INPUT = "prices";

    @Singleton
    @Named("price-stream")
    KStream<UUID, ProductPriceEvent> priceStream(ConfiguredStreamBuilder builder) {

        Properties props = builder.getConfiguration();
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.UUID().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KStream<UUID, ProductPriceEvent> source = builder.stream(INPUT);
        source.foreach((uid, price) -> log.info("Product {} has new price {}p", price.getProductId(), price.getPrice()));
        return source;
    }
}
