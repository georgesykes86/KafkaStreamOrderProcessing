package kafka.stream.order.processing.domain.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.stream.order.processing.domain.model.ProductData;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class ProductsProvider {

    public ProductData getProductData(){
        try(InputStream in=Thread.currentThread().getContextClassLoader().getResourceAsStream("products.json")){
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(in, ProductData.class);
        }
        catch(Exception e){
            return new ProductData();
        }
    }

}
