package kafka.stream.order.processing.domain.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Introspected
public class ProductPriceRequest {

    long price;
    String productId;

}
