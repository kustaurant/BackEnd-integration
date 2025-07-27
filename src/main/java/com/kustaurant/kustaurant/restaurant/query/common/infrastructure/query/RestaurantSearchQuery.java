package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query;

import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.*;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantMenuEntity.restaurantMenuEntity;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantSearchQuery {

    private final JPAQueryFactory queryFactory;

    public List<Integer> searchRestaurantIds(String[] kwArr, int size) {
        BooleanBuilder andBuilder = new BooleanBuilder();
        for (String kw : kwArr) {
            String pattern = "%" + kw + "%";

            BooleanBuilder orBuilder = new BooleanBuilder()
                    .or(restaurantEntity.restaurantCuisine.like(pattern))
                    .or(restaurantEntity.restaurantName.like(pattern))
                    .or(restaurantEntity.restaurantType.like(pattern));

            BooleanExpression menuExists = JPAExpressions
                    .selectOne()
                    .from(restaurantMenuEntity)
                    .where(
                            restaurantMenuEntity.restaurantId.eq(restaurantEntity.restaurantId),
                            restaurantMenuEntity.menuName.like(pattern)
                    )
                    .exists();

            andBuilder.and(orBuilder.or(menuExists));
        }

        return queryFactory
                .select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .where(andBuilder)
                .limit(size)
                .fetch();
    }
}
