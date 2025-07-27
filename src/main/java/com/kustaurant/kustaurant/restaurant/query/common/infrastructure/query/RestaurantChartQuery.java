package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query;

import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query.RestaurantCommonExpressions.restaurantActive;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query.RestaurantCommonExpressions.situationMatches;
import static java.util.Objects.isNull;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantChartQuery {

    private final JPAQueryFactory queryFactory;

    /**
     * 조건(conditino)을 만족하는 식당 id들을 반환
     */
    public Page<Integer> getRestaurantIdsWithPage(ChartCondition condition, Pageable pageable) {

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
                        hasSituation(condition.situations(), restaurantEntity),
                        restaurantActive(restaurantEntity)
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
                        hasSituation(condition.situations(), restaurantEntity),
                        restaurantActive(restaurantEntity)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }


    public List<Integer> getRestaurantIds(ChartCondition condition) {

        NumberExpression<Double> avgScore = new CaseBuilder()
                .when(restaurantEntity.mainTier.gt(0)
                        .and(restaurantEntity.restaurantEvaluationCount.isNotNull())
                        .and(restaurantEntity.restaurantEvaluationCount.gt(0)))
                .then(restaurantEntity.restaurantScoreSum
                        .divide(restaurantEntity.restaurantEvaluationCount))
                .otherwise(0.0);

        return queryFactory.select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .where(
                        cuisinesIn(condition.cuisines()),
                        positionsIn(condition.positions()),
                        hasSituation(condition.situations(), restaurantEntity),
                        restaurantActive(restaurantEntity)
                )
                .orderBy(avgScore.desc())
                .fetch();
    }

    private BooleanExpression cuisinesIn(List<String> cuisines) {
        if (isNull(cuisines) || cuisines.isEmpty()) {
            return null;
        }
        if (cuisines.contains("JH")) {
            return restaurantEntity.partnershipInfo.isNotNull().and(restaurantEntity.partnershipInfo.ne(""));
        }
        return restaurantEntity.restaurantCuisine.in(cuisines);
    }

    private BooleanExpression positionsIn(List<String> positions) {
        return isNull(positions) || positions.isEmpty() ? null : restaurantEntity.restaurantPosition.in(positions);
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
}
