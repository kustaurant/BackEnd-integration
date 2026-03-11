package com.kustaurant.kustaurant.admin.crawl;

import com.kustaurant.jpa.restaurant.entity.QRestaurantPartnershipEntity;
import com.kustaurant.jpa.restaurant.entity.RestaurantPartnershipEntity;
import com.kustaurant.jpa.restaurant.enums.MatchStatus;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminPartnershipQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<RestaurantPartnershipEntity> searchPartnerships(
            PartnershipTarget target,
            MatchStatus matchStatus,
            String sourceAccount,
            String keyword,
            Pageable pageable
    ) {

        QRestaurantPartnershipEntity p = QRestaurantPartnershipEntity.restaurantPartnershipEntity;

        BooleanBuilder builder = new BooleanBuilder();

        if (target != null) {
            builder.and(p.target.eq(target));
        }

        if (matchStatus != null) {
            builder.and(p.matchStatus.eq(matchStatus));
        }

        if (sourceAccount != null && !sourceAccount.isBlank()) {
            builder.and(p.sourceAccount.eq(sourceAccount));
        }

        if (keyword != null && !keyword.isBlank()) {
            builder.and(
                    p.restaurantName.containsIgnoreCase(keyword)
            );
        }

        List<RestaurantPartnershipEntity> content = queryFactory
                .selectFrom(p)
                .where(builder)
                .orderBy(p.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(p.count())
                .from(p)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(
                content,
                pageable,
                total == null ? 0 : total
        );
    }
}
