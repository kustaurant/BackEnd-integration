package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

public abstract class RestaurantCommonExpressions {

    /**
     * 식당의 상황 리스트 조건
     */
    public static BooleanExpression situationMatches(
            QRestaurantSituationRelationEntity rs, NumberPath<Long> restaurantId) {
        return rs.restaurantId.eq(restaurantId)
                .and(rs.dataCount.goe(RestaurantConstants.SITUATION_GOE));
    }

    public static BooleanExpression restaurantActive(
            QRestaurantEntity r
    ) {
        return r.status.eq("ACTIVE");
    }

    public static Expression<Long> evaluationCount(
            JPAQueryFactory queryFactory, QEvaluationEntity e, NumberPath<Long> restaurantId
    ) {
        return queryFactory
                .select(e.count().coalesce(0L))
                .from(e)
                .where(e.restaurantId.eq(restaurantId));
    }
}
