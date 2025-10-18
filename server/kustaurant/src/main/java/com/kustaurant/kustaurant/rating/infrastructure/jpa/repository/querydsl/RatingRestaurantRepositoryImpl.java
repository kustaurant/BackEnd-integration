package com.kustaurant.kustaurant.rating.infrastructure.jpa.repository.querydsl;

import static com.kustaurant.jpa.restaurant.entity.QRestaurantEntity.restaurantEntity;
import com.kustaurant.kustaurant.rating.service.port.RatingRestaurantRepository;
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
}
