package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository;

import static com.kustaurant.jpa.rating.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.jpa.restaurant.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions.restaurantActive;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions.*;

import com.kustaurant.jpa.rating.entity.QRatingEntity;
import com.kustaurant.kustaurant.restaurant.query.chart.service.port.RestaurantChartRepository;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.observation.annotation.Observed;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RestaurantChartRepositoryImpl implements RestaurantChartRepository {

    private final JPAQueryFactory queryFactory;
    private final RestaurantCommonExpressions restaurantCommonExpressions;

    /**
     * 조건(condition)을 만족하는 식당 id들을 반환
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Observed
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
                        orderByProcess(ratingEntity, condition)
                )
                .offset(offset)
                .limit(pageSize)
                .fetch();

        Long total = queryFactory
                .select(restaurantEntity.restaurantId.countDistinct())
                .from(restaurantEntity)
                .leftJoin(ratingEntity)
                .on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where( cuisinesIn(condition.cuisines(), restaurantEntity),
                        positionsIn(condition.positions(), restaurantEntity),
                        restaurantCommonExpressions.hasSituation(condition.situations(), restaurantEntity),
                        restaurantActive(restaurantEntity),
                        tierFilterProcess(ratingEntity, condition)
                )
                .fetchOne();

        return new PageImpl<>(contents, pageable, total == null ? 0 : total);
    }

    private static OrderSpecifier[] orderByProcess(QRatingEntity ratingEntity, ChartCondition condition) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        orders.add(ratingEntity.hasTier.coalesce(false).desc());
        if (!condition.aiTier()) {
            orders.add(ratingEntity.isTemp.coalesce(true).asc());
        }
        orders.add(ratingEntity.tier.coalesce(-1).asc());
        orders.add(ratingEntity.finalScore.coalesce(0.0).desc());

        return orders.toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression tierFilterProcess(QRatingEntity ratingEntity, ChartCondition condition) {
        BooleanExpression base = null;
        // 1 전체
        if (condition.needAll()) {
            return base;
        }
        // 2 티어가 있는 것만
        if (condition.needOnlyTier()) {
            base = append(base, ratingEntity.tier.coalesce(-1).gt(0));
            // 2-1 자체 티어만
            if (!condition.aiTier()) {
                return append(base, ratingEntity.isTemp.coalesce(true).eq(false));
            }
            // 2-2 AI 티어 포함
            return base;
        }
        // 3 티어가 없는 것만
        base = append(base, ratingEntity.tier.coalesce(-1).lt(0));
        // 3-1 자체 티어가 없는 것(AI 티어 포함)
        if (!condition.aiTier()) {
            return base.or(ratingEntity.isTemp.coalesce(true).eq(true));
        }
        // 3-2 자체 티어와 AI 티어 모두 없는 것
        return base;
    }

    private BooleanExpression append(BooleanExpression base, BooleanExpression extra) {
        return base == null ? extra : base.and(extra);
    }
}
