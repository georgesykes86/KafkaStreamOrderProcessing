package kafka.stream.order.processing.domain.joiner;

import kafka.stream.order.processing.domain.model.IndividualOrderItem;
import kafka.stream.order.processing.domain.model.PricedOrderItem;
import kafka.stream.order.processing.domain.model.PricedProduct;
import org.apache.kafka.streams.kstream.ValueJoiner;


public class OrderProductJoiner implements ValueJoiner<IndividualOrderItem, PricedProduct, PricedOrderItem> {

    @Override
    public PricedOrderItem apply(IndividualOrderItem orderItem, PricedProduct pricedProduct) {
        return PricedOrderItem.builder()
                .orderId(orderItem.getOrderId())
                .id(orderItem.getProductId())
                .name(pricedProduct.getName())
                .quantity(orderItem.getQuantity())
                .price(pricedProduct.getPrice())
                .build();
    }
}

