package dev.barahow.core.dto;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Product {
    private UUID id;
    private String title;
    private double price;
    private Integer quantity;
    private String description;
    private String category;
    private String image;
    private  Rating rating;




}
