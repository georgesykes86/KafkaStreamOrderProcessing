package kafka.stream.order.processing.domain.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Value;

@Value
@Introspected
public class ProductPriceRequest {

    long price;
    String productId;

}
