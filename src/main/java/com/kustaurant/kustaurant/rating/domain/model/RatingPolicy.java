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
        @Valid EvaluationPolicy evaluation,
        @Valid RestaurantPolicy restaurant,
        @Valid TierPolicy tier
) {
    public record EvaluationPolicy(
            @DecimalMin("0.0") @DecimalMax("1.0") double recencyLambda,
            @Min(1) double reactionDivisor,
            @Min(1) int evaluatorK,
            @Valid CompletenessWeight completenessWeight
    ) {
        public record CompletenessWeight(
                @DecimalMin("0.0") @DecimalMax("1.0") double comment,
                @DecimalMin("0.0") @DecimalMax("1.0") double situation,
                @DecimalMin("0.0") @DecimalMax("1.0") double image
        ) {}
    }

    public record RestaurantPolicy(
            @Valid PopularityWeight popularityWeight
    ) {
        public record PopularityWeight(
                @DecimalMin("0.0") double visits,
                @DecimalMin("0.0") double favorites,
                @DecimalMin("0.0") double evaluations
        ) {}
    }

    public record TierPolicy(
            @NotEmpty @Size(min=5, max=5) Map<@Min(1) @Max(5) Integer, @Valid TierLevel> levels
    ) {
        public record TierLevel(
                @DecimalMin("0.0") double minScore,
                @DecimalMin("0.0") double maxRatio
        ) {}
    }
}
