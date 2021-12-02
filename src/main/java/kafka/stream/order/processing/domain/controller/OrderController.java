package kafka.stream.order.processing.domain.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import kafka.stream.order.processing.domain.model.Order;
import kafka.stream.order.processing.domain.model.OrderItem;
import kafka.stream.order.processing.domain.model.OrderItemRequest;
import kafka.stream.order.processing.domain.model.OrderRequest;
import kafka.stream.order.processing.domain.producer.OrderClient;

import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;

@Controller("/orders")
public class OrderController {

    private OrderClient client;

    @Inject
    public OrderController(OrderClient client) {
        this.client = client;
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Post
    public HttpResponse registerNewOrder(OrderRequest request) {
        UUID orderId = UUID.randomUUID();
        client.sendOrder(orderId, mapToOrder(orderId, request));
        return HttpResponse.ok();
    }

    private Order mapToOrder(UUID orderId, OrderRequest request) {
        return Order.newBuilder()
                .setId(orderId.toString())
                .setItems(request.getItems().stream()
                        .map(this::getOrderItem)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderItem getOrderItem(OrderItemRequest requestOrderItem) {
        return OrderItem.newBuilder()
                .setProductId(requestOrderItem.getProductId())
                .setQuantity(requestOrderItem.getQuantity())
                .build();
    }

}
