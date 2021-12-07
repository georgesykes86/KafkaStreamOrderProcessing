package kafka.stream.order.processing.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class DetailedOrder {

    String id;
    List<PricedOrderItem> items;
    long totalCost;

}
