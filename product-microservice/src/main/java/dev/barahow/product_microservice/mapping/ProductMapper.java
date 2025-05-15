package dev.barahow.product_microservice.mapping;

import dev.barahow.core.dto.Product;
import dev.barahow.product_microservice.dao.ProductEntity;

public interface ProductMapper {
    Product toDTO(ProductEntity productEntity);

    ProductEntity toEntity(Product productDTO);
}
