package com.kustaurant.kustaurant.rating.domain.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "restaurant.rating.policy.tier")
public record TierPolicyProp(
        @NotEmpty @Size(min=4, max=4)
        Map<@Min(1) @Max(4) Integer, @Valid TierLevel> levels
) {

    public record TierLevel(
            @DecimalMin("0.0") double minScore,
            @DecimalMin("0.0") double maxRatio
    ) {}
}
