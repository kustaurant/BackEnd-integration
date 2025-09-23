package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository;

import static com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions.restaurantActive;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.*;

import com.kustaurant.kustaurant.restaurant.query.home.RestaurantHomeRepository;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RestaurantHomeRepositoryImpl implements RestaurantHomeRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Long> getTopRestaurantIds(int size) {
        return queryFactory.select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where(restaurantActive(restaurantEntity))
                .orderBy(ratingEntity.score.desc())
                .limit(size)
                .fetch();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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
