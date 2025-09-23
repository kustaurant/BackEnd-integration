package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository;

import static com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions.restaurantActive;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions.*;

import com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.QRatingEntity;
import com.kustaurant.kustaurant.restaurant.query.chart.service.port.RestaurantChartRepository;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RestaurantChartRepositoryImpl implements RestaurantChartRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 조건(condition)을 만족하는 식당 id들을 반환
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<Long> getRestaurantIdsWithPage(ChartCondition condition) {

        JPAQuery<Long> query = queryFactory.select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .leftJoin(ratingEntity)
                .on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where(
                        cuisinesIn(condition.cuisines(), restaurantEntity),
                        positionsIn(condition.positions(), restaurantEntity),
                        hasSituation(condition.situations(), restaurantEntity),
                        restaurantActive(restaurantEntity),
                        tierFilterProcess(ratingEntity, condition)
                )
                .orderBy(ratingEntity.score.desc());
        // pageable unpaged 처리
        Pageable pageable = condition.pageable();
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
        }

        List<Long> content = query.fetch();

        Long total = queryFactory
                .select(restaurantEntity.restaurantId.countDistinct())
                .from(restaurantEntity)
                .where(
                        cuisinesIn(condition.cuisines(), restaurantEntity),
                        positionsIn(condition.positions(), restaurantEntity),
                        hasSituation(condition.situations(), restaurantEntity),
                        restaurantActive(restaurantEntity)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression tierFilterProcess(QRatingEntity ratingEntity, ChartCondition condition) {
        if (condition.needAll()) {
            return null;
        }
        if (condition.needOnlyTier()) {
            return ratingEntity.tier.gt(0);
        }
        return ratingEntity.tier.lt(0);
    }
}
