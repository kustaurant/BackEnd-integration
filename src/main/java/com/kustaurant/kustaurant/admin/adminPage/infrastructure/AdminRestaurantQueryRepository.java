package com.kustaurant.kustaurant.admin.adminPage.infrastructure;

import com.kustaurant.kustaurant.admin.adminPage.controller.response.PagedRestaurantResponse;
import com.kustaurant.kustaurant.admin.adminPage.controller.response.RestaurantListResponse;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.*;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;

@Repository
@RequiredArgsConstructor
public class AdminRestaurantQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PagedRestaurantResponse getAllRestaurants(Pageable pageable) {

        Expression<Long> evaluationCount = queryFactory
                .select(evaluationEntity.count().coalesce(0L))
                .from(evaluationEntity)
                .where(evaluationEntity.restaurantId.eq(restaurantEntity.restaurantId));

        List<RestaurantListResponse> restaurants = queryFactory
                .select(Projections.constructor(RestaurantListResponse.class,
                        restaurantEntity.restaurantId,
                        restaurantEntity.restaurantName,
                        restaurantEntity.restaurantAddress,
                        restaurantEntity.restaurantPosition,
                        restaurantEntity.restaurantType,
                        restaurantEntity.status,
                        evaluationCount,
                        restaurantEntity.createdAt
                ))
                .from(restaurantEntity)
                .orderBy(restaurantEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalElements = queryFactory
                .select(restaurantEntity.count())
                .from(restaurantEntity)
                .fetchOne();

        totalElements = totalElements != null ? totalElements : 0L;
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        return new PagedRestaurantResponse(
                restaurants,
                totalElements,
                totalPages,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getPageNumber() < totalPages - 1,
                pageable.getPageNumber() > 0
        );
    }

    public Long getTotalRestaurants() {
        Long count = queryFactory
                .select(restaurantEntity.count())
                .from(restaurantEntity)
                .fetchOne();
        
        return count != null ? count : 0L;
    }
}