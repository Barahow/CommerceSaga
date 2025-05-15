package dev.barahow.product_microservice.mapping;

import dev.barahow.core.dto.Rating;
import dev.barahow.product_microservice.dao.RatingEntity;

public class RatingMapper {
    public Rating toDTO(RatingEntity ratingEntity){
        if(ratingEntity==null){
            return null;
        }

        return  Rating.builder()
                .rate(ratingEntity.getRate())
                .count(ratingEntity.getCount()).build();
    }

    public RatingEntity toEntity(Rating rating) {
        if (rating==null){
            return null;
        }

        return RatingEntity.builder().rate(rating.getRate())
                .count(rating.getCount()).build();
    }
}
