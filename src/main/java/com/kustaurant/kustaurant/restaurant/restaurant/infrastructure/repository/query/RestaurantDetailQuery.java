package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.query;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity.restaurantSituationRelationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QSituationEntity.situationEntity;
import static com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantFavoriteEntity.restaurantFavoriteEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantMenuEntity.restaurantMenuEntity;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static java.util.Objects.isNull;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.QRestaurantMenu;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.QRestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantDetailQuery {

    private final JPAQueryFactory queryFactory;

    public Optional<RestaurantDetail> getRestaurantDetails(Long restaurantId, Long userId) {
        JPAQuery<?> q = queryFactory
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .leftJoin(restaurantMenuEntity)
                .on(menuRestaurantIdEq(restaurantEntity.restaurantId))
                .leftJoin(restaurantSituationRelationEntity)
                .on(situationMatches(restaurantEntity.restaurantId))
                .leftJoin(situationEntity)
                .on(situationIdEq(restaurantSituationRelationEntity.situationId))
                .leftJoin(evaluationEntity)
                .on(evaluationRestaurantIdEq(restaurantEntity.restaurantId, userId))
                .leftJoin(restaurantFavoriteEntity)
                .on(favoriteRestaurantIdEq(restaurantEntity.restaurantId, userId));

        Map<Long, RestaurantDetail> result = q.where(restaurantIdEq(restaurantId))
                .transform(
                        groupBy(restaurantEntity.restaurantId).as(
                                new QRestaurantDetail(
                                        restaurantEntity.restaurantId,
                                        restaurantEntity.restaurantImgUrl,
                                        ratingEntity.tier.coalesce(0),
                                        restaurantEntity.restaurantCuisine,
                                        restaurantEntity.restaurantPosition,
                                        restaurantEntity.restaurantName,
                                        restaurantEntity.restaurantAddress,
                                        restaurantEntity.restaurantUrl,
                                        set(situationEntity.situationName),
                                        restaurantEntity.partnershipInfo,
                                        Expressions.constant(getEvaluationCount(restaurantId)),
                                        ratingEntity.score.coalesce(0.0),
                                        evaluationEntity.isNotNull(),
                                        restaurantFavoriteEntity.isNotNull(),
                                        Expressions.constant(getFavoriteCount(restaurantId)),
                                        set(new QRestaurantMenu(
                                                restaurantMenuEntity.id,
                                                restaurantMenuEntity.restaurantId,
                                                restaurantMenuEntity.menuName,
                                                restaurantMenuEntity.menuPrice,
                                                restaurantMenuEntity.naverType,
                                                restaurantMenuEntity.menuImgUrl
                                        )),
                                        restaurantEntity.restaurantType,
                                        restaurantEntity.restaurantTel,
                                        restaurantEntity.visitCount,
                                        restaurantEntity.latitude,
                                        restaurantEntity.longitude
                                )
                        )
                );
        if (!result.containsKey(restaurantId)) {
            return Optional.empty();
        }

        return Optional.of(result.get(restaurantId));
    }

    public Long getFavoriteCount(Long restaurantId) {
        Long favoriteCount = queryFactory
                .select(restaurantFavoriteEntity.id.count())
                .from(restaurantFavoriteEntity)
                .where(restaurantFavoriteEntity.restaurantId.eq(restaurantId))
                .fetchOne();
        if (isNull(favoriteCount)) {
            favoriteCount = 0L;
        }
        return favoriteCount;
    }

    public Integer getEvaluationCount(Long restaurantId) {
        Long evaluationCount = queryFactory
                .select(evaluationEntity.id.count())
                .from(evaluationEntity)
                .where(evaluationEntity.restaurantId.eq(restaurantId))
                .fetchOne();
        if (isNull(evaluationCount)) {
            evaluationCount = 0L;
        }
        return evaluationCount.intValue();
    }

    private BooleanExpression restaurantIdEq(Long restaurantId) {
        return restaurantEntity.restaurantId.eq(restaurantId);
    }

    private BooleanExpression menuRestaurantIdEq(NumberPath<Long> restaurantId) {
        return restaurantMenuEntity.restaurantId.eq(restaurantId);
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

    /**
     * 식당의 상황 리스트 조건
     */
    private BooleanExpression situationMatches(NumberPath<Long> restaurantId) {
        return restaurantSituationRelationEntity.restaurantId.eq(restaurantId)
                .and(restaurantSituationRelationEntity.dataCount.goe(RestaurantConstants.SITUATION_GOE));
    }
}
