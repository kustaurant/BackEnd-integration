package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity.restaurantSituationRelationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QSituationEntity.situationEntity;
import static com.kustaurant.kustaurant.restaurant.favorite.infrastructure.QRestaurantFavoriteEntity.restaurantFavoriteEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantMenuEntity.restaurantMenuEntity;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static java.util.Objects.isNull;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.QRestaurantMenu;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.query.RestaurantDetailQuery;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.QRestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantDetailRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantDetailRepositoryImpl implements RestaurantDetailRepository {

    private final RestaurantDetailQuery restaurantDetailQuery;

    @Override
    public RestaurantDetail getRestaurantDetail(Integer restaurantId, Long userId) {
        if (isNull(restaurantId)) {
            throw new IllegalArgumentException("restaurantId is null");
        }

        Long favoriteCount = restaurantDetailQuery.getFavoriteCount(restaurantId);

        Optional<RestaurantDetail> optional = restaurantDetailQuery.getRestaurantDetails(
                restaurantId, userId, favoriteCount);

        if (optional.isEmpty()) {
            throw new DataNotFoundException(ErrorCode.RESTAURANT_NOT_FOUND, restaurantId, "식당");
        }

        return optional.get();
    }
}
