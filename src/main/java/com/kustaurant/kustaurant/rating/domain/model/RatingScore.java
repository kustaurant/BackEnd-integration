package com.kustaurant.kustaurant.rating.domain.model;

public record RatingScore(
        long restaurantId,
        double score
) {

}
