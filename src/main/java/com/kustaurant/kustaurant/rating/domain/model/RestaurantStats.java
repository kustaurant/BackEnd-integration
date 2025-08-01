package com.kustaurant.kustaurant.rating.domain.model;

import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy.RestaurantPolicy;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy.RestaurantPolicy.PopularityScale;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy.RestaurantPolicy.PopularityWeight;

public record RestaurantStats(
        int restaurantId,
        int visitCount,
        int favoriteCount,
        int evaluationCount
) {

    public double adjustPopularity(RestaurantPolicy policy, double score) {
        PopularityWeight weight = policy.popularityWeight();
        PopularityScale scale = policy.popularityScale();
        double popularity = (weight.visits() * (1 + Math.tanh(Math.log1p(visitCount) / scale.visit()))
                + weight.favorites() * (1 + Math.tanh(Math.log1p(favoriteCount) / scale.favorite()))
                + weight.evaluations() * (1 + Math.tanh(Math.log1p(evaluationCount) / scale.evaluation())))
                / 3;

        return score * popularity;
    }
}
