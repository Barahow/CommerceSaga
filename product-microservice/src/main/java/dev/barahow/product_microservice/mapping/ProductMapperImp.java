package dev.barahow.product_microservice.mapping;

import dev.barahow.core.dto.Product;
import dev.barahow.product_microservice.dao.ProductEntity;

public class ProductMapperImp implements ProductMapper{

private  final RatingMapper ratingMapper= new RatingMapper();
    @Override
    public Product toDTO(ProductEntity productEntity) {
        if (productEntity==null) {
            return null;
        }


        return Product.builder().id(productEntity.getId())
                .title(productEntity.getTitle())
                .category(productEntity.getCategory())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice())
                .image(productEntity.getImage())
                .rating(ratingMapper.toDTO(productEntity.getRating())).build();
    }

    @Override
    public ProductEntity toEntity(Product productDTO) {
        if(productDTO==null){
            return null;
        }

        return ProductEntity.builder().id(productDTO.getId())
                .title(productDTO.getTitle())
                .category(productDTO.getCategory())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .image(productDTO.getImage())
                .rating(ratingMapper.toEntity(productDTO.getRating()))
                .build();
    }
}
