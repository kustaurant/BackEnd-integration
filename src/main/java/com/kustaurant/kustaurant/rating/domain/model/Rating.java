package com.kustaurant.kustaurant.rating.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record Rating(
        int restaurantId,
        double score,
        Tier tier,
        boolean isTemp,
        LocalDateTime ratedAt
) {

}
