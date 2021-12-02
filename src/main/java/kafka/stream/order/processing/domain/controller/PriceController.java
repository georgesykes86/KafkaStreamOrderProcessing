package kafka.stream.order.processing.domain.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import kafka.stream.order.processing.domain.model.ProductPriceEvent;
import kafka.stream.order.processing.domain.model.ProductPriceRequest;
import kafka.stream.order.processing.domain.producer.PriceClient;

import java.util.UUID;
import javax.inject.Inject;

@Controller("/prices")
public class PriceController {

    private PriceClient client;

    @Inject
    public PriceController(PriceClient client) {
        this.client = client;
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Post
    public HttpResponse setNewPrice(ProductPriceRequest request) {
        client.sendPrice(UUID.randomUUID(), mapToPriceEvent(request));
        return HttpResponse.ok();
    }

    private ProductPriceEvent mapToPriceEvent(ProductPriceRequest request) {
        return ProductPriceEvent.newBuilder().setPrice(request.getPrice()).setProductId(request.getProductId()).build();
    }
}
