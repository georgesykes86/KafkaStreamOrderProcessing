package kafka.stream.order.processing;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import kafka.stream.order.processing.domain.provider.ProductsProvider;
import kafka.stream.order.processing.domain.model.Product;
import kafka.stream.order.processing.domain.model.ProductPriceEvent;
import kafka.stream.order.processing.domain.producer.PriceClient;
import kafka.stream.order.processing.domain.producer.ProductClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class OrderProducerStarter {

    private final ProductClient productClient;
    private final PriceClient priceClient;
    private final ProductsProvider provider;

    @Inject
    public OrderProducerStarter(ProductClient productClient, PriceClient priceClient, ProductsProvider provider) {
        this.productClient = productClient;
        this.priceClient = priceClient;
        this.provider = provider;
    }

    @EventListener
    public void startProducingOrders(final StartupEvent event) throws InterruptedException {
        log.info("Starting the producer");

        List<Product> products = provider.getProductData().getProducts();

        products.forEach(product -> {
            productClient.sendProduct(UUID.randomUUID(), product);
            ProductPriceEvent initialPrice = new ProductPriceEvent(product.getId(), randomPriceInPence());
            priceClient.sendPrice(UUID.randomUUID(), initialPrice);
        });
    }

    public long randomPriceInPence() {
        long min = 1L;
        long max = 500L;
        return min + (long) (Math.random() * (max - min));
    }

}
