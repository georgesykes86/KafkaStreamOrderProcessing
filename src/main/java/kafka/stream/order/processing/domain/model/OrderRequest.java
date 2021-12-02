package kafka.stream.order.processing.domain.model;

import lombok.Value;

import java.util.List;

@Value
public class OrderRequest {

    List<OrderItemRequest> items;

}
