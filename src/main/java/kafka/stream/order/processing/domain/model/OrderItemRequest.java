package kafka.stream.order.processing.domain.model;

import lombok.Value;

@Value
public class OrderItemRequest {

    String productId;
    int quantity;

}
