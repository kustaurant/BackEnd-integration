package com.kustaurant.kustaurant.rating.infrastructure.jpa.repository.querydsl;

import static com.kustaurant.jpa.restaurant.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.jpa.restaurant.entity.QRestaurantFavoriteEntity.restaurantFavoriteEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;

import com.kustaurant.kustaurant.rating.domain.model.QRestaurantStats;
import com.kustaurant.kustaurant.rating.domain.model.RestaurantStats;
import com.kustaurant.kustaurant.rating.service.port.RatingRestaurantRepository;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RatingRestaurantRepositoryImpl implements RatingRestaurantRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> getRestaurantIds() {
        return queryFactory
                .select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .where(restaurantEntity.status.eq("ACTIVE"))
                .fetch();
    }

    @Override
    public List<RestaurantStats> getRestaurantStatsByIds(List<Long> ids) {

        Expression<Long> evaluationCount = JPAExpressions
                .select(evaluationEntity.count())
                .from(evaluationEntity)
                .where(
                        evaluationEntity.restaurantId.eq(restaurantEntity.restaurantId)
                                .and(evaluationEntity.status.eq("ACTIVE"))
                );

        Expression<Long> favoriteCount = JPAExpressions
                .select(restaurantFavoriteEntity.count())
                .from(restaurantFavoriteEntity)
                .where(
                        restaurantFavoriteEntity.restaurantId.eq(restaurantEntity.restaurantId)
                                .and(restaurantFavoriteEntity.status.eq("ACTIVE"))
                );

        return queryFactory
                .select(new QRestaurantStats(
                        restaurantEntity.restaurantId,
                        restaurantEntity.visitCount,
                        favoriteCount,
                        evaluationCount
                        ))
                .from(restaurantEntity)
                .where(restaurantEntity.restaurantId.in(ids)
                                .and(restaurantEntity.status.eq("ACTIVE")))
                .fetch();
    }
}
