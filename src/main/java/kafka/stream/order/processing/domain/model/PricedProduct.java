package kafka.stream.order.processing.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class PricedProduct {

    String id;
    String name;
    long price;

}
