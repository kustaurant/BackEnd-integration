package com.kustaurant.kustaurant.rating.domain.model;

import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy.RestaurantPolicy;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy.RestaurantPolicy.PopularityScale;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy.RestaurantPolicy.PopularityWeight;
import com.querydsl.core.annotations.QueryProjection;

public record RestaurantStats(
        int restaurantId,
        int visitCount,
        long favoriteCount,
        long evaluationCount
) {

    @QueryProjection
    public RestaurantStats(int restaurantId, int visitCount, long favoriteCount, long evaluationCount) {
        this.restaurantId = restaurantId;
        this.visitCount = visitCount;
        this.favoriteCount = favoriteCount;
        this.evaluationCount = evaluationCount;
    }

    public double adjustPopularity(RestaurantPolicy policy, double score) {
        PopularityWeight weight = policy.popularityWeight();
        PopularityScale scale = policy.popularityScale();
        double popularity = (weight.visits() * (1 + Math.tanh(Math.log1p(visitCount) / scale.visit()))
                + weight.favorites() * (1 + Math.tanh(Math.log1p(favoriteCount) / scale.favorite()))
                + weight.evaluations() * (1 + Math.tanh(Math.log1p(evaluationCount) / scale.evaluation())))
                / (weight.visits() + weight.favorites() + weight.evaluations());

        return score * popularity;
    }
}
