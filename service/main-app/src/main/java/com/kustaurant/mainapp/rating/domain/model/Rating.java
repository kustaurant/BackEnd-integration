package com.kustaurant.mainapp.rating.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record Rating(
        long restaurantId,
        double score,
        Tier tier,
        boolean isTemp,
        LocalDateTime ratedAt
) {

}
