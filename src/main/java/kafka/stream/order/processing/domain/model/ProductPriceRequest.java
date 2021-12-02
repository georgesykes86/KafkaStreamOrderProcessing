package kafka.stream.order.processing.domain.model;

import lombok.Value;

@Value
public class ProductPriceRequest {

    long price;
    String productId;

}
