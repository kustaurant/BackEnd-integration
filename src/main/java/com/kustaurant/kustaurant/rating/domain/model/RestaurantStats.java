package com.kustaurant.kustaurant.rating.domain.model;

import java.util.List;

public record RestaurantStats(
        int restaurantId,
        int visitCount,
        int favoriteCount,
        int evaluationCount,
        List<EvaluationWithContext> evaluations
) {

}
