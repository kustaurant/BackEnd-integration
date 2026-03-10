package com.kustaurant.kustaurant.rating.infrastructure.jpa.mapper;

import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.jpa.rating.entity.RatingEntity;

public class RatingMapper {

    public static RatingEntity from(Rating rating) {
        return new RatingEntity(
                rating.getRestaurantId(),
                rating.getScore(),
                rating.getTier().getValue(),
                rating.isTemp(),
                rating.getRatedAt(),
                rating.getFinalScore()
        );
    }
}
