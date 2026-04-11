package com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.rating.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.restaurant.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.restaurant.entity.QRestaurantFavoriteEntity.restaurantFavoriteEntity;
import static com.kustaurant.restaurant.entity.QRestaurantMenuEntity.restaurantMenuEntity;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForEngine;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForSearch;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RestaurantSearchRepositoryImpl implements RestaurantSearchRepository {

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Long> searchRestaurantIds(String[] kwArr, int size) {
        BooleanBuilder andBuilder = new BooleanBuilder();
        for (String kw : kwArr) {
            String pattern = "%" + kw + "%";

            BooleanBuilder orBuilder = new BooleanBuilder()
                    .or(restaurantEntity.restaurantCuisine.like(pattern))
                    .or(restaurantEntity.restaurantName.like(pattern))
                    .or(restaurantEntity.restaurantType.like(pattern));

            BooleanExpression menuExists = JPAExpressions
                    .selectOne()
                    .from(restaurantMenuEntity)
                    .where(
                            restaurantMenuEntity.restaurantId.eq(restaurantEntity.restaurantId),
                            restaurantMenuEntity.menuName.like(pattern)
                    )
                    .exists();

            andBuilder.and(orBuilder.or(menuExists));
        }

        return queryFactory
                .select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .where(andBuilder)
                .limit(size)
                .fetch();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<RestaurantForEngine> getRestaurantForEngine() {
        Map<Long, RestaurantForEngine> map = queryFactory
                .from(restaurantEntity)
                .leftJoin(restaurantMenuEntity)
                .on(restaurantMenuEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where(restaurantEntity.status.eq("ACTIVE"))
                .transform(groupBy(restaurantEntity.restaurantId).as(
                        com.querydsl.core.types.Projections.constructor(
                                RestaurantForEngine.class,
                                restaurantEntity.restaurantId,
                                restaurantEntity.restaurantName,
                                restaurantEntity.restaurantCuisine,
                                list(restaurantMenuEntity.menuName)
                        )
                ));

        return new ArrayList<>(map.values());
    }

    @Override
    public Map<Long, RestaurantForSearch> getRestaurantForSearch(List<Long> restaurantIds, @Nullable Long userId) {
        return queryFactory
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where(restaurantEntity.restaurantId.in(restaurantIds))
                .transform(groupBy(restaurantEntity.restaurantId).as(
                        com.querydsl.core.types.Projections.constructor(
                                RestaurantForSearch.class,
                                restaurantEntity.restaurantId,
                                restaurantEntity.restaurantName,
                                restaurantEntity.restaurantCuisine,
                                restaurantEntity.restaurantPosition,
                                restaurantEntity.restaurantImgUrl,
                                ratingEntity.tier,
                                restaurantEntity.partnershipInfo,
                                userId == null
                                        ? Expressions.asBoolean(false)
                                        : JPAExpressions
                                                .selectOne()
                                                .from(evaluationEntity)
                                                .where(
                                                        evaluationEntity.userId.eq(userId),
                                                        evaluationEntity.restaurantId.eq(restaurantEntity.restaurantId)
                                                )
                                                .exists(),
                                userId == null
                                        ? Expressions.asBoolean(false)
                                        : JPAExpressions
                                                .selectOne()
                                                .from(restaurantFavoriteEntity)
                                                .where(
                                                        restaurantFavoriteEntity.userId.eq(userId),
                                                        restaurantFavoriteEntity.restaurantId.eq(restaurantEntity.restaurantId),
                                                        restaurantFavoriteEntity.status.eq("ACTIVE")
                                                )
                                                .exists()
                        )
                ));
    }
}
