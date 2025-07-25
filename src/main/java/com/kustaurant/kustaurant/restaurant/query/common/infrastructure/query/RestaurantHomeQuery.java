package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query;

import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query.RestaurantCommonExpressions.restaurantActive;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.*;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantHomeQuery {

    private final JPAQueryFactory queryFactory;

    public List<Integer> getTopRestaurantIds(int size) {
        // 정렬 기준
        NumberExpression<Double> avgScore = new CaseBuilder()
                .when(restaurantEntity.mainTier.gt(0)
                        .and(restaurantEntity.restaurantEvaluationCount.isNotNull())
                        .and(restaurantEntity.restaurantEvaluationCount.gt(0)))
                .then(restaurantEntity.restaurantScoreSum
                        .divide(restaurantEntity.restaurantEvaluationCount))
                .otherwise(0.0);

        return queryFactory.select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .where(restaurantActive(restaurantEntity))
                .orderBy(avgScore.desc())
                .limit(size)
                .fetch();
    }

    public List<Integer> getRandomRestaurantIds(int size) {
        return queryFactory.select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .where(restaurantActive(restaurantEntity))
                .orderBy(
                        Expressions.numberTemplate(Double.class, "RAND()").asc()
                )
                .limit(size)
                .fetch();
    }
}
