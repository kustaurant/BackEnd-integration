package com.kustaurant.mainapp.rating.domain.model;

public record RatingScore(
        long restaurantId,
        double score
) {

}
