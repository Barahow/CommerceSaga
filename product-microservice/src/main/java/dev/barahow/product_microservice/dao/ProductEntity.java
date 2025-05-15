package dev.barahow.product_microservice.dao;

import jakarta.persistence.*;

import lombok.*;


import java.util.UUID;

@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductEntity {

    @Id
   @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID id;

    @Column(nullable=false)
    private String title;
    @Column(nullable=false)
    private double price;

    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable=false)
    private String description;
    @Column(nullable=false)
    private String category;
    @Column(nullable=false)
    private String image;

    // its a single object so we should use @Embedded
    @Embedded
    private RatingEntity rating;



}
