package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository;

import static com.kustaurant.jpa.rating.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.jpa.restaurant.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.global.exception.ErrorCode.RESTAURANT_NOT_FOUND;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions.*;

import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.draw.RestaurantDrawRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.mapper.RestaurantMapper;
import com.kustaurant.jpa.restaurant.repository.RestaurantJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RestaurantDrawRepositoryImpl implements RestaurantDrawRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final JPAQueryFactory queryFactory;
    private final RestaurantCommonExpressions restaurantCommonExpressions;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Restaurant getById(Long id) {
        return restaurantJpaRepository.findByRestaurantIdAndStatus(id, "ACTIVE")
                .map(RestaurantMapper::toModel)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_NOT_FOUND, id, "식당"));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Long> getRestaurantIds(ChartCondition condition) {

        return queryFactory.select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where(
                        cuisinesIn(condition.cuisines(), restaurantEntity),
                        positionsIn(condition.positions(), restaurantEntity),
                        restaurantCommonExpressions.hasSituation(condition.situations(), restaurantEntity),
                        restaurantActive(restaurantEntity),
                        ratingEntity.isTemp.not()
                )
                .orderBy(ratingEntity.finalScore.desc())
                .fetch();
    }
}
