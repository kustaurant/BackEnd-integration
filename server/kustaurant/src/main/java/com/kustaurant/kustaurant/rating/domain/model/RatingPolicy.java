package com.kustaurant.kustaurant.rating.domain.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "restaurant.rating.policy")
public record RatingPolicy(
        @Min(0) int priorWeight,
        @Valid EvaluationPolicy evaluation,
        @Valid RestaurantPolicy restaurant,
        @Valid TierPolicy tier
) {
    public record EvaluationPolicy(
            @DecimalMin("0.0") @DecimalMax("1.0") double recencyLambda,
            @Max(1) @Min(0) double recencyBoostGamma,
            @Max(1) @Min(0) double minDecay,
            @Min(1) double reactionDivisor,
            @Max(1) @Min(0) double reactionScale,
            @Min(1) int evaluatorK,
            @Valid CompletenessWeight completenessWeight
    ) {

        public double getRecencyBoostW(long ageDays) {
            double decay = Math.exp(-recencyLambda * ageDays);
            return Math.max(minDecay, decay);
        }

        public double getDecayW(long ageDays) {
            return 1 + recencyBoostGamma * Math.exp(-recencyLambda * ageDays);
        }

        public double getReactionW(long reactionScore) {
            double score = Math.max(-reactionDivisor, Math.min(reactionDivisor, reactionScore));
            return 1 + reactionScale * Math.tanh(score / reactionDivisor);
        }

        public double getEvaluationReliabilityW(long evalCount) {
            return evalCount / (double)(evalCount + evaluatorK);
        }

        public record CompletenessWeight(
                @DecimalMin("0.1") @DecimalMax("1.0") double score,
                @DecimalMin("0.0") @DecimalMax("1.0") double comment,
                @DecimalMin("0.0") @DecimalMax("1.0") double situation,
                @DecimalMin("0.0") @DecimalMax("1.0") double image
        ) {
            public double getWeightSum() {
                return score + comment + situation + image;
            }
        }
    }

    public record RestaurantPolicy(
            @Valid PopularityScale popularityScale,
            @Valid PopularityWeight popularityWeight
    ) {

        public record PopularityScale(
                @Min(1) int visit,
                @Min(1) int favorite,
                @Min(1) int evaluation
        ) {}

        public record PopularityWeight(
                @DecimalMin("0.0") double visits,
                @DecimalMin("0.0") double favorites,
                @DecimalMin("0.0") double evaluations
        ) {

            double getWeightSum() {
                return visits + favorites + evaluations;
            }
        }
    }

    public record TierPolicy(
            @NotEmpty @Size(min=4, max=4) Map<@Min(1) @Max(4) Integer, @Valid TierLevel> levels
    ) {
        public record TierLevel(
                @DecimalMin("0.0") double minScore,
                @DecimalMin("0.0") double maxRatio
        ) {}
    }
}
