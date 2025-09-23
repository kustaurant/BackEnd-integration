package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity.restaurantSituationRelationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QSituationEntity.situationEntity;
import static com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions.situationMatches;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantFavoriteEntity.restaurantFavoriteEntity;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RestaurantCoreInfoRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 식당 id들에 대해 식당 요약 정보(RestaurantCoreInfoDto)를 반환함.
     * 기본적인 식당 정보 + (userId가 존재하면) 평가 여부 + 즐찾 여부 등
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<RestaurantCoreInfoDto> getRestaurantTiers(List<Long> restaurantIds, Long userId) {
        Map<Long, RestaurantCoreInfoDto> map = queryFactory
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
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
                                        ratingEntity.tier,
                                        evaluationEntity.isNotNull(),
                                        restaurantFavoriteEntity.isNotNull(),
                                        restaurantEntity.longitude,
                                        restaurantEntity.latitude,
                                        restaurantEntity.partnershipInfo,
                                        ratingEntity.score,
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

    private BooleanExpression evaluationRestaurantIdEq(NumberPath<Long> restaurantId, Long userId) {
        return isNull(userId) ? Expressions.FALSE
                : evaluationEntity.restaurantId.eq(restaurantId).and(evaluationEntity.userId.eq(userId));
    }

    private BooleanExpression favoriteRestaurantIdEq(NumberPath<Long> restaurantId, Long userId) {
        return isNull(userId) ? Expressions.FALSE
                : restaurantFavoriteEntity.restaurantId.eq(restaurantId)
                        .and(restaurantFavoriteEntity.userId.eq(userId))
                        .and(restaurantFavoriteEntity.status.eq("ACTIVE"));
    }

    private BooleanExpression situationIdEq(NumberPath<Long> situationId) {
        return isNull(situationId) ? Expressions.FALSE : situationEntity.situationId.eq(situationId);
    }
}
