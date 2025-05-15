package dev.barahow.product_microservice.service;

import dev.barahow.core.dto.Product;
import dev.barahow.core.exceptions.InsufficientProductQuantityException;
import dev.barahow.core.exceptions.OrderNotFoundException;
import dev.barahow.core.exceptions.ProductNotFoundException;
import dev.barahow.product_microservice.mapping.ProductMapper;
import dev.barahow.product_microservice.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import dev.barahow.product_microservice.dao.ProductEntity;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImp(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        Page<ProductEntity> fetchAllProducts= productRepository.findAll(pageable);
        if(fetchAllProducts.isEmpty()){
            throw new ProductNotFoundException("No Products found");
        }
        return fetchAllProducts.map(productMapper::toDTO);
    }

    @Override
    public Product reserve(Product desiredProduct, UUID orderId) {
      ProductEntity productEntity= productRepository.findById(desiredProduct.getId()).orElseThrow(()-> new ProductNotFoundException("Product with that id not found"));
      if (desiredProduct.getQuantity()> productEntity.getQuantity()){
          throw new InsufficientProductQuantityException("Product out of stock");
      }

      Product reserveProduct= productMapper.toDTO(productEntity);
      reserveProduct.setQuantity(desiredProduct.getQuantity());

      return reserveProduct;

    }

    @Override
    public void cancelReservation(Product productToCancel, UUID orderId) {
        ProductEntity productEntity= productRepository.findById(productToCancel.getId()).orElseThrow(()-> new OrderNotFoundException("Order with that id not found"));

        productEntity.setQuantity(productEntity.getQuantity()+productToCancel.getQuantity());

        productRepository.save(productEntity);

    }

    @Override
    public Product save(Product product) {
      ProductEntity saveProduct=  productMapper.toEntity(product);
       productRepository.save(saveProduct);


        return productMapper.toDTO(saveProduct);
    }

    @Override
    public Page<Product> findByCategory(String category, Pageable pageable) {
        Page<ProductEntity> productEntities= productRepository.findByCategory(category,pageable);

        return productEntities.map(productMapper::toDTO);
    }

    @Override
    public Page<Product> findByTitleOrDescription(String title, String descKeyword, Pageable pageable) {
        Page<ProductEntity> productEntities= productRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(title,descKeyword,pageable);



        return productEntities.map(productMapper::toDTO);
    }

    @Override
    public Page<Product> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable) {
        Page<ProductEntity> productEntities= productRepository.findByPriceBetween(minPrice,maxPrice,pageable);

        return productEntities.map(productMapper::toDTO);
    }

    @Override
    public Page<Product> findAllByPriceAsc(Pageable pageable) {
        Page<ProductEntity> productEntities= productRepository.findAllByOrderByPriceAsc(pageable);

        return productEntities.map(productMapper::toDTO);
    }

    @Override
    public Page<Product> findAllByPriceDesc(Pageable pageable) {

        Page<ProductEntity> productEntities= productRepository.findAllByOrderByPriceDesc(pageable);

        return productEntities.map(productMapper::toDTO);
    }

    @Override
    public Page<Product> findAllRatingAsc(Pageable pageable) {
        Page<ProductEntity> productEntities= productRepository.findAllByOrderByRatingAsc(pageable);

        return productEntities.map(productMapper::toDTO);
    }

    @Override
    public Page<Product> findAllByRatingDesc(Pageable pageable) {
        Page<ProductEntity> productEntities= productRepository.findAllByOrderByRatingDesc(pageable);

        return productEntities.map(productMapper::toDTO);
    }


}
