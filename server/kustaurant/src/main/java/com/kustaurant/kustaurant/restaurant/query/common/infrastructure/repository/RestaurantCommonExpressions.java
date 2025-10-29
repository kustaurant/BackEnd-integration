package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository;

import static java.util.Objects.isNull;

import com.kustaurant.jpa.restaurant.entity.QRestaurantEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Cuisine;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestaurantCommonExpressions {

    private final Integer SITUATION_MIN_COUNT;

    public RestaurantCommonExpressions(@Value("${restaurant.situation_min_count}") Integer situationMinCount) {
        this.SITUATION_MIN_COUNT = situationMinCount;
    }

    /**
     * 식당의 상황 리스트 조건
     */
    public BooleanExpression situationMatches(
            QRestaurantSituationRelationEntity rs, NumberPath<Long> restaurantId) {
        return rs.restaurantId.eq(restaurantId)
                .and(rs.dataCount.goe(SITUATION_MIN_COUNT));
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

    public BooleanExpression hasSituation(List<Long> situations, QRestaurantEntity r) {
        if (isNull(situations) || situations.isEmpty()) {
            return null;
        }

        QRestaurantSituationRelationEntity rs = new QRestaurantSituationRelationEntity("reSub");
        return JPAExpressions
                .selectOne()
                .from(rs)
                .where(
                        situationMatches(rs, r.restaurantId),
                        rs.situationId.in(situations)
                )
                .exists();
    }

    public static BooleanExpression cuisinesIn(List<String> cuisines, QRestaurantEntity r) {
        if (isNull(cuisines) || cuisines.isEmpty()) {
            return null;
        }
        if (cuisines.contains(Cuisine.JH.name())) {
            return r.partnershipInfo.isNotNull().and(r.partnershipInfo.ne(""));
        }
        return r.restaurantCuisine.in(cuisines);
    }

    public static BooleanExpression positionsIn(List<String> positions, QRestaurantEntity r) {
        return isNull(positions) || positions.isEmpty() ? null : r.restaurantPosition.in(positions);
    }
}
