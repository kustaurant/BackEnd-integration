package com.kustaurant.kustaurant.evaluation.evaluation.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantSituationRelation {

    private Long relationId;

    private Long situationId;
    private Integer restaurantId;
    private Integer dataCount;

    public static RestaurantSituationRelation create(Long situationId, Integer restaurantId, Integer dataCount) {
        return RestaurantSituationRelation.builder()
                .situationId(situationId)
                .restaurantId(restaurantId)
                .dataCount(dataCount)
                .build();
    }

    public void addDataCount(Integer dataCount) {
        this.dataCount += dataCount;
    }
}
