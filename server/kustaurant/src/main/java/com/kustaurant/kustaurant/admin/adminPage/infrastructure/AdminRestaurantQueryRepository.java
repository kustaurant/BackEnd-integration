package com.kustaurant.kustaurant.admin.adminPage.infrastructure;

import com.kustaurant.kustaurant.admin.IGCrawl.controller.query.PagedRestaurantResponse;
import com.kustaurant.kustaurant.admin.IGCrawl.controller.query.PartnershipListResponse;
import com.kustaurant.kustaurant.admin.adminPage.controller.response.RestaurantListResponse;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.*;
import static com.kustaurant.restaurant.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.restaurant.entity.QRestaurantPartnershipEntity.restaurantPartnershipEntity;

@Repository
@RequiredArgsConstructor
public class AdminRestaurantQueryRepository {

    private final JPAQueryFactory qf;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PagedRestaurantResponse getAllRestaurants(Pageable pageable) {

        Expression<Long> evaluationCount = qf
                .select(evaluationEntity.count().coalesce(0L))
                .from(evaluationEntity)
                .where(evaluationEntity.restaurantId.eq(restaurantEntity.restaurantId));

        List<RestaurantListResponse> restaurants = qf
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

        Long totalElements = qf
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

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<PartnershipListResponse> getAllPartnerships(Pageable pageable) {

        List<PartnershipListResponse> content = qf
                .select(Projections.constructor(PartnershipListResponse.class,
                        restaurantPartnershipEntity.id,
                        restaurantPartnershipEntity.restaurantId,
                        restaurantPartnershipEntity.restaurantName,
                        restaurantPartnershipEntity.target.stringValue(),
                        restaurantPartnershipEntity.benefit,
                        restaurantPartnershipEntity.matchStatus.stringValue(),
                        restaurantPartnershipEntity.postUrl,
                        restaurantPartnershipEntity.createdAt,
                        restaurantPartnershipEntity.updatedAt
                ))
                .from(restaurantPartnershipEntity)
                .orderBy(restaurantPartnershipEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalElements = qf
                .select(restaurantPartnershipEntity.count())
                .from(restaurantPartnershipEntity)
                .fetchOne();

        long total = totalElements != null ? totalElements : 0L;

        return new PageImpl<>(content, pageable, total);
    }

    public Long getTotalRestaurants() {
        Long count = qf
                .select(restaurantEntity.count())
                .from(restaurantEntity)
                .fetchOne();
        
        return count != null ? count : 0L;
    }
}