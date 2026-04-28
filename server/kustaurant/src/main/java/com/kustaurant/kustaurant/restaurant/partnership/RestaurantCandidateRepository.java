package com.kustaurant.kustaurant.restaurant.partnership;

import com.kustaurant.kustaurant.admin.IGCrawl.dto.RestaurantMatchCandidate;
import com.kustaurant.kustaurant.admin.IGCrawl.dto.RestaurantPhoneMatch;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.kustaurant.restaurant.entity.QRestaurantEntity.restaurantEntity;

@Repository
@RequiredArgsConstructor
public class RestaurantCandidateRepository {
    private final JPAQueryFactory queryFactory;

    public List<RestaurantPhoneMatch> findIdsByPhoneNumbers(Collection<String> phoneNumbers) {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(Projections.constructor(
                        RestaurantPhoneMatch.class,
                        restaurantEntity.restaurantId,
                        restaurantEntity.restaurantTel
                ))
                .from(restaurantEntity)
                .where(restaurantEntity.restaurantTel.in(phoneNumbers))
                .fetch();
    }

    public Optional<RestaurantMatchCandidate> findMatchCandidateById(Long id) {
        RestaurantMatchCandidate result = queryFactory
                .select(Projections.constructor(
                        RestaurantMatchCandidate.class,
                        restaurantEntity.restaurantId,
                        restaurantEntity.restaurantName,
                        restaurantEntity.restaurantAddress,   // ← 실제 필드명으로 바꿔
                        restaurantEntity.restaurantTel
                ))
                .from(restaurantEntity)
                .where(restaurantEntity.restaurantId.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    public List<RestaurantMatchCandidate> findMatchCandidatesByIds(Iterable<Long> ids) {
        List<Long> idList = new ArrayList<>();
        for (Long id : ids) {
            if (id != null) {
                idList.add(id);
            }
        }

        if (idList.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(Projections.constructor(
                        RestaurantMatchCandidate.class,
                        restaurantEntity.restaurantId,
                        restaurantEntity.restaurantName,
                        restaurantEntity.restaurantAddress,   // ← 실제 필드명으로 바꿔
                        restaurantEntity.restaurantTel
                ))
                .from(restaurantEntity)
                .where(restaurantEntity.restaurantId.in(idList))
                .fetch();
    }
}
