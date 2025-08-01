package com.kustaurant.kustaurant.rating.domain.model;

public record ScoringPolicy(
        double globalAvg,
        double recencyLambda,
        int evaluatorK,
        double commentWeight,
        double situationWeight,
        double imageWeight
) {

}
