package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.query;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity.restaurantSituationRelationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QSituationEntity.situationEntity;
import static com.kustaurant.kustaurant.restaurant.favorite.infrastructure.QRestaurantFavoriteEntity.restaurantFavoriteEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantMenuEntity.restaurantMenuEntity;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static java.util.Objects.isNull;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.QRestaurantMenu;
import com.kustaurant.kustaurant.restaurant.restaurant.service.constants.RestaurantConstants;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.QRestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantDetail;
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

    public Optional<RestaurantDetail> getRestaurantDetails(Integer restaurantId, Long userId,
            Long favoriteCount) {
        JPAQuery<?> q = queryFactory
                .from(restaurantEntity)
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

        Map<Integer, RestaurantDetail> result = q.where(restaurantIdEq(restaurantId))
                .transform(
                        groupBy(restaurantEntity.restaurantId).as(
                                new QRestaurantDetail(
                                        restaurantEntity.restaurantId,
                                        restaurantEntity.restaurantImgUrl,
                                        restaurantEntity.mainTier,
                                        restaurantEntity.restaurantCuisine,
                                        restaurantEntity.restaurantPosition,
                                        restaurantEntity.restaurantName,
                                        restaurantEntity.restaurantAddress,
                                        restaurantEntity.restaurantUrl,
                                        set(situationEntity.situationName),
                                        restaurantEntity.partnershipInfo,
                                        restaurantEntity.restaurantEvaluationCount,
                                        restaurantEntity.restaurantScoreSum,
                                        evaluationEntity.isNotNull(),
                                        restaurantFavoriteEntity.isNotNull(),
                                        Expressions.constant(favoriteCount),
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

    public Long getFavoriteCount(Integer restaurantId) {
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

    private BooleanExpression restaurantIdEq(Integer restaurantId) {
        return restaurantEntity.restaurantId.eq(restaurantId);
    }

    private BooleanExpression menuRestaurantIdEq(NumberPath<Integer> restaurantId) {
        return restaurantMenuEntity.restaurantId.eq(restaurantId);
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

    /**
     * 식당의 상황 리스트 조건
     */
    private BooleanExpression situationMatches(NumberPath<Integer> restaurantId) {
        return restaurantSituationRelationEntity.restaurantId.eq(restaurantId)
                .and(restaurantSituationRelationEntity.dataCount.goe(RestaurantConstants.SITUATION_GOE));
    }
}
