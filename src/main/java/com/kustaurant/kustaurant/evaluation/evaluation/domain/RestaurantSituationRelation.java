package com.kustaurant.kustaurant.evaluation.evaluation.domain;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantSituationRelation {

    private Long relationId;
    private Integer dataCount;
    private Long situationId;
    private Integer restaurantId;
}
