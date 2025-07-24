package com.kustaurant.kustaurant.restaurant.tier.infrastructure.query;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity.restaurantSituationRelationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QSituationEntity.situationEntity;
import static com.kustaurant.kustaurant.restaurant.favorite.infrastructure.QRestaurantFavoriteEntity.restaurantFavoriteEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantMenuEntity.restaurantMenuEntity;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static java.util.Objects.isNull;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity;
import com.kustaurant.kustaurant.restaurant.tier.dto.QRestaurantTierDTO;
import com.kustaurant.kustaurant.restaurant.tier.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.restaurant.tier.service.port.ChartCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantChartQuery {

    private final JPAQueryFactory queryFactory;

    public Page<Integer> getRestaurantIds(ChartCondition condition, Pageable pageable) {

        NumberExpression<Double> avgScore = new CaseBuilder()
                .when(restaurantEntity.mainTier.gt(0)
                        .and(restaurantEntity.restaurantEvaluationCount.isNotNull())
                        .and(restaurantEntity.restaurantEvaluationCount.gt(0)))
                .then(restaurantEntity.restaurantScoreSum
                        .divide(restaurantEntity.restaurantEvaluationCount))
                .otherwise(0.0);

        List<Integer> content = queryFactory.select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .where(
                        cuisinesIn(condition.cuisines()),
                        positionsIn(condition.positions()),
                        hasSituation(condition.situations(), restaurantEntity)
                )
                .orderBy(avgScore.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(restaurantEntity.restaurantId.countDistinct())
                .from(restaurantEntity)
                .where(
                        cuisinesIn(condition.cuisines()),
                        positionsIn(condition.positions()),
                        hasSituation(condition.situations(), restaurantEntity)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression cuisinesIn(List<String> cuisines) {
        if (isNull(cuisines)) {
            return null;
        }
        if (cuisines.contains("JH")) {
            return restaurantEntity.partnershipInfo.isNotNull().and(restaurantEntity.partnershipInfo.ne(""));
        }
        return restaurantEntity.restaurantCuisine.in(cuisines);
    }

    private BooleanExpression positionsIn(List<String> positions) {
        return isNull(positions) ? null : restaurantEntity.restaurantPosition.in(positions);
    }

    private BooleanExpression hasSituation(List<Long> situations, QRestaurantEntity r) {
        if (isNull(situations) || situations.isEmpty()) {
            return null;
        }

        QRestaurantSituationRelationEntity rs = new QRestaurantSituationRelationEntity("reSub");
        return JPAExpressions
                .selectOne()
                .from(rs)
                .where(
                        situationMatches(rs, r.restaurantId),
                        rs.situationId.in(situations)
                )
                .exists();
    }

    // -------------------------------------------------------------

    public List<RestaurantTierDTO> getRestaurantTiers(List<Integer> restaurantIds, Long userId) {
        Map<Integer, RestaurantTierDTO> map = queryFactory
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
                                new QRestaurantTierDTO(
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

    /**
     * 식당의 상황 리스트 조건
     */
    private BooleanExpression situationMatches(QRestaurantSituationRelationEntity rs, NumberPath<Integer> restaurantId) {
        return rs.restaurantId.eq(restaurantId)
                .and(rs.dataCount.goe(RestaurantConstants.SITUATION_GOE));
    }

}
