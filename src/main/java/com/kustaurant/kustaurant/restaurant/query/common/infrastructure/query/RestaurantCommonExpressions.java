package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;

public abstract class RestaurantCommonExpressions {

    /**
     * 식당의 상황 리스트 조건
     */
    public static BooleanExpression situationMatches(
            QRestaurantSituationRelationEntity rs, NumberPath<Integer> restaurantId) {
        return rs.restaurantId.eq(restaurantId)
                .and(rs.dataCount.goe(RestaurantConstants.SITUATION_GOE));
    }

    public static BooleanExpression restaurantActive(
            QRestaurantEntity r
    ) {
        return r.status.eq("ACTIVE");
    }
}
