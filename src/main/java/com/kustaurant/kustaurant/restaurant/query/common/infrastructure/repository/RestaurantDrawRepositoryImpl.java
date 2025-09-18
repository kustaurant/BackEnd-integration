package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.RESTAURANT_NOT_FOUND;
import static com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions.*;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;

import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.draw.RestaurantDrawRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.RestaurantJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantDrawRepositoryImpl implements RestaurantDrawRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Restaurant getById(Long id) {
        return restaurantJpaRepository.findByRestaurantIdAndStatus(id, "ACTIVE")
                .map(RestaurantEntity::toModel)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_NOT_FOUND, id, "식당"));
    }

    @Override
    public List<Long> getRestaurantIds(ChartCondition condition) {

        return queryFactory.select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where(
                        cuisinesIn(condition.cuisines(), restaurantEntity),
                        positionsIn(condition.positions(), restaurantEntity),
                        hasSituation(condition.situations(), restaurantEntity),
                        restaurantActive(restaurantEntity)
                )
                .orderBy(ratingEntity.score.desc())
                .fetch();
    }
}
