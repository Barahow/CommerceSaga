package dev.barahow.product_microservice.repository;

import dev.barahow.product_microservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import dev.barahow.product_microservice.dao.ProductEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    Page<ProductEntity> findByCategory(String category, Pageable pageable);

    Page<ProductEntity> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String descKeyword, Pageable pageable);

    Page<ProductEntity> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable);

    Page<ProductEntity> findAllByOrderByPriceAsc(Pageable pageable);

    Page<ProductEntity> findAllByOrderByPriceDesc(Pageable pageable);

    // For rating queries, also add Pageable
    @Query("SELECT p FROM ProductEntity p ORDER BY p.rating.rate ASC")
    Page<ProductEntity> findAllByOrderByRatingAsc(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p ORDER BY p.rating.rate DESC")
    Page<ProductEntity> findAllByOrderByRatingDesc(Pageable pageable);


}
