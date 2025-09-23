package com.kustaurant.mainapp.evaluation.evaluation.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantSituationRelation {

    private Long relationId;

    private Long situationId;
    private Long restaurantId;
    private Integer dataCount;

    public static RestaurantSituationRelation create(Long situationId, Long restaurantId, Integer dataCount) {
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
