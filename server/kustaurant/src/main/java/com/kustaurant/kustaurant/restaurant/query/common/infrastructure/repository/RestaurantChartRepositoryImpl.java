package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository;

import static com.kustaurant.jpa.rating.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.jpa.restaurant.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions.restaurantActive;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions.*;

import com.kustaurant.jpa.rating.entity.QRatingEntity;
import com.kustaurant.kustaurant.restaurant.query.chart.service.port.RestaurantChartRepository;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RestaurantChartRepositoryImpl implements RestaurantChartRepository {

    private final JPAQueryFactory queryFactory;
    private final RestaurantCommonExpressions restaurantCommonExpressions;

    /**
     * 조건(condition)을 만족하는 식당 id들을 반환
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<Long> getRestaurantIdsWithPage(ChartCondition condition) {
        Pageable pageable = condition.pageable() != null ? condition.pageable() : Pageable.unpaged();

        int pageSize = pageable.isPaged() ? pageable.getPageSize() : Integer.MAX_VALUE;
        long offset = pageable.isPaged() ? pageable.getOffset() : 0;

        List<Long> contents = queryFactory.select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .leftJoin(ratingEntity)
                .on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where(
                        cuisinesIn(condition.cuisines(), restaurantEntity),
                        positionsIn(condition.positions(), restaurantEntity),
                        restaurantCommonExpressions.hasSituation(condition.situations(), restaurantEntity),
                        restaurantActive(restaurantEntity),
                        tierFilterProcess(ratingEntity, condition)
                )
                .orderBy(
                        // 티어가 있는 것이 먼저 오고, 티어 기준 오름차순 정렬
                        new CaseBuilder()
                                .when(ratingEntity.tier.lt(0)).then(1)
                                .otherwise(0)
                                .asc(),
                        ratingEntity.tier.asc(),
                        ratingEntity.finalScore.desc()
                )
                .offset(offset)
                .limit(pageSize)
                .fetch();

        Long total = queryFactory
                .select(restaurantEntity.restaurantId.countDistinct())
                .from(restaurantEntity)
                .where( cuisinesIn(condition.cuisines(), restaurantEntity),
                        positionsIn(condition.positions(), restaurantEntity),
                        restaurantCommonExpressions.hasSituation(condition.situations(),
                                restaurantEntity), restaurantActive(restaurantEntity)
                )
                .fetchOne();

        return new PageImpl<>(contents, pageable, total == null ? 0 : total);
    }

    private BooleanExpression tierFilterProcess(QRatingEntity ratingEntity, ChartCondition condition) {
        BooleanExpression base = null;
        if (!condition.aiTier()) {
            base = ratingEntity.isTemp.eq(false);
        }
        if (condition.needAll()) {
            return base;
        }
        if (condition.needOnlyTier()) {
            return append(base, ratingEntity.tier.gt(0));
        }
        return append(base, ratingEntity.tier.lt(0));
    }

    private BooleanExpression append(BooleanExpression base, BooleanExpression extra) {
        return base == null ? extra : base.and(extra);
    }
}
