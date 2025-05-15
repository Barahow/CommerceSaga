package dev.barahow.product_microservice.service;

import dev.barahow.core.dto.Product;
import dev.barahow.product_microservice.dao.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface ProductService {
    Page<Product> findAll(Pageable pageable);
    Product reserve(Product desiredProduct, UUID orderId);
    void cancelReservation(Product productToCancel, UUID orderId);
    Product save(Product product);



    Page<Product> findByCategory(String category, Pageable pageable);

    Page<Product> findByTitleOrDescription(String title, String descKeyword, Pageable pageable);

    Page<Product> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable);

    Page<Product> findAllByPriceAsc(Pageable pageable);

    Page<Product> findAllByPriceDesc(Pageable pageable);

    // For rating queries, also add Pageable

    Page<Product> findAllRatingAsc(Pageable pageable);


    Page<Product> findAllByRatingDesc(Pageable pageable);

}
