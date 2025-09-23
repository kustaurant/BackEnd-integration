package com.kustaurant.mainapp.rating.infrastructure.jpa.mapper;

import com.kustaurant.mainapp.rating.domain.model.Rating;
import com.kustaurant.jpa.rating.entity.RatingEntity;

public class RatingMapper {

    public static RatingEntity from(Rating rating) {
        return new RatingEntity(
                rating.restaurantId(),
                rating.score(),
                rating.tier().getValue(),
                rating.isTemp(),
                rating.ratedAt()
        );
    }
}
