package dev.barahow.product_microservice.dao;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RatingEntity {
    private double rate;
    private  int count;
}
