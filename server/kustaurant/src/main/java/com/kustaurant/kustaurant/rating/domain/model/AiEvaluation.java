package com.kustaurant.kustaurant.rating.domain.model;

import com.querydsl.core.annotations.QueryProjection;

public record AiEvaluation(
        long restaurantId,
        double positiveRatio,
        double negativeRatio,
        double aiScoreAvg
) {

    @QueryProjection
    public AiEvaluation(long restaurantId, double positiveRatio, double negativeRatio, double aiScoreAvg) {
        this.restaurantId = restaurantId;
        this.positiveRatio = positiveRatio;
        this.negativeRatio = negativeRatio;
        this.aiScoreAvg = aiScoreAvg;
    }

    public double getCoverage() {
        return positiveRatio + negativeRatio;
    }
}
