package kafka.stream.order.processing.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class IndividualOrderItem {

    String orderId;
    String productId;
    int quantity;

}
