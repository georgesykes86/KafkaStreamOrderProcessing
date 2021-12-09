package kafka.stream.order.processing.domain.joiner;

import kafka.stream.order.processing.domain.model.IndividualOrderItem;
import kafka.stream.order.processing.domain.model.PricedOrderItem;
import kafka.stream.order.processing.domain.model.PricedProduct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.ValueJoiner;

@Slf4j
public class OrderProductJoiner implements ValueJoiner<IndividualOrderItem, PricedProduct, PricedOrderItem> {

    @Override
    public PricedOrderItem apply(IndividualOrderItem orderItem, PricedProduct pricedProduct) {
        log.info("inside the joiner with {} and {}", orderItem, pricedProduct);
        return PricedOrderItem.newBuilder()
                .setOrderId(orderItem.getOrderId())
                .setId(orderItem.getProductId())
                .setName(pricedProduct.getName())
                .setQuantity(orderItem.getQuantity())
                .setPrice(pricedProduct.getPrice())
                .build();
    }
}

