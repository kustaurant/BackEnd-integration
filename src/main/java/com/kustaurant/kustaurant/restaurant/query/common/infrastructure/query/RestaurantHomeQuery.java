package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query;

import static com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.QRatingEntity.ratingEntity;
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

    public List<Long> getTopRestaurantIds(int size) {
        return queryFactory.select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where(restaurantActive(restaurantEntity))
                .orderBy(ratingEntity.score.desc())
                .limit(size)
                .fetch();
    }

    public List<Long> getRandomRestaurantIds(int size) {
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
