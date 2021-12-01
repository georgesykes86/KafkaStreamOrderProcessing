package kafka.stream.order.processing.data.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.io.ResourceLoader;
import kafka.stream.order.processing.domain.model.Products;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Collections;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class ProductsProvider {

    private ResourceLoader resourceLoader;
    private ObjectMapper objectMapper;

    @Inject
    public ProductsProvider(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    public Products getProductData(){
        return resourceLoader.getResourceAsStream("products.json")
                .map(this::mapStreamToProducts)
                .orElse(Products.builder().products(Collections.emptyList()).build());
    }

    public Products mapStreamToProducts(InputStream stream) {
        try {
            return objectMapper.readValue(stream, Products.class);
        } catch (Exception e) {
            log.info("Exception parsing products {}", e.getMessage());
            return Products.builder().products(Collections.emptyList()).build();
        }

    }

}
