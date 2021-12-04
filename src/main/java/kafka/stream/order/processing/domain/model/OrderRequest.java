package kafka.stream.order.processing.domain.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@Introspected
public class OrderRequest {

    List<OrderItemRequest> items;

}
