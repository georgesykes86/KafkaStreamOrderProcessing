package kafka.stream.order.processing.domain.model;

import lombok.Builder;

import java.util.List;

@Builder
public class Products {

    List<Product> products;

}
