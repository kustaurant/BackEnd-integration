package com.kustaurant.kustaurant.rating.domain.model;

import lombok.Builder;

@Builder
public record Rating(
        int restaurantId,
        double score,
        Tier tier,
        boolean isTemp
) {

}
