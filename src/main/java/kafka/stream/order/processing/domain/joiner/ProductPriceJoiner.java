package kafka.stream.order.processing.domain.joiner;

import kafka.stream.order.processing.domain.model.PricedProduct;
import kafka.stream.order.processing.domain.model.Product;
import kafka.stream.order.processing.domain.model.ProductPriceEvent;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class ProductPriceJoiner implements ValueJoiner<Product, ProductPriceEvent, PricedProduct> {

    @Override
    public PricedProduct apply(Product product, ProductPriceEvent price) {
        return PricedProduct.builder().id(product.getId()).name(product.getName()).price(price.getPrice()).build();
    }
}

