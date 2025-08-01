package com.kustaurant.kustaurant.rating.infrastructure.jpa;

import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;

import com.kustaurant.kustaurant.rating.domain.model.RestaurantStats;
import com.kustaurant.kustaurant.rating.service.port.RatingRestaurantRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RatingRestaurantRepositoryImpl implements RatingRestaurantRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Integer> getRestaurantIds() {
        return queryFactory
                .select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .where(restaurantEntity.status.eq("ACTIVE"))
                .fetch();
    }

    @Override
    public Map<Integer, RestaurantStats> getRestaurantStatsByIds(List<Integer> ids) {
        return Map.of();
    }
}
