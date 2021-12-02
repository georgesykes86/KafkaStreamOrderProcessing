package kafka.stream.order.processing.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import java.util.List;

@Data
public class ProductData {

    @JsonValue
    List<Product> products;

}
