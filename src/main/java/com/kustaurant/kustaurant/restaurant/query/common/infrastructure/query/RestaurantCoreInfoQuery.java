package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity.restaurantSituationRelationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QSituationEntity.situationEntity;
import static com.kustaurant.kustaurant.restaurant.favorite.infrastructure.QRestaurantFavoriteEntity.restaurantFavoriteEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query.RestaurantCommonExpressions.situationMatches;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static java.util.Objects.isNull;

import com.kustaurant.kustaurant.restaurant.query.common.dto.QRestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantCoreInfoQuery {

    private final JPAQueryFactory queryFactory;

    public List<RestaurantCoreInfoDto> getRestaurantTiers(List<Integer> restaurantIds, Long userId) {
        Map<Integer, RestaurantCoreInfoDto> map = queryFactory
                .from(restaurantEntity)
                .leftJoin(restaurantSituationRelationEntity)
                .on(situationMatches(restaurantSituationRelationEntity,
                        restaurantEntity.restaurantId))
                .leftJoin(situationEntity)
                .on(situationIdEq(restaurantSituationRelationEntity.situationId))
                .leftJoin(evaluationEntity)
                .on(evaluationRestaurantIdEq(restaurantEntity.restaurantId, userId))
                .leftJoin(restaurantFavoriteEntity)
                .on(favoriteRestaurantIdEq(restaurantEntity.restaurantId, userId))
                .where(restaurantEntity.restaurantId.in(restaurantIds))
                .transform(
                        groupBy(restaurantEntity.restaurantId).as(
                                new QRestaurantCoreInfoDto(
                                        restaurantEntity.restaurantId,
                                        restaurantEntity.restaurantName,
                                        restaurantEntity.restaurantCuisine,
                                        restaurantEntity.restaurantPosition,
                                        restaurantEntity.restaurantImgUrl,
                                        restaurantEntity.mainTier,
                                        evaluationEntity.isNotNull(),
                                        restaurantFavoriteEntity.isNotNull(),
                                        restaurantEntity.longitude,
                                        restaurantEntity.latitude,
                                        restaurantEntity.partnershipInfo,
                                        restaurantEntity.restaurantScoreSum,
                                        restaurantEntity.restaurantEvaluationCount,
                                        set(situationEntity.situationName),
                                        restaurantEntity.restaurantType
                                )
                        )
                );

        return restaurantIds.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private BooleanExpression evaluationRestaurantIdEq(NumberPath<Integer> restaurantId, Long userId) {
        return isNull(userId) ? Expressions.FALSE
                : evaluationEntity.restaurantId.eq(restaurantId).and(evaluationEntity.userId.eq(userId));
    }

    private BooleanExpression favoriteRestaurantIdEq(NumberPath<Integer> restaurantId, Long userId) {
        return isNull(userId) ? Expressions.FALSE
                : restaurantFavoriteEntity.restaurantId.eq(restaurantId).and(restaurantFavoriteEntity.userId.eq(userId));
    }

    private BooleanExpression situationIdEq(NumberPath<Long> situationId) {
        return isNull(situationId) ? Expressions.FALSE : situationEntity.situationId.eq(situationId);
    }
}
