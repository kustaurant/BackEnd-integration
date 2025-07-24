package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static java.util.Objects.isNull;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.query.RestaurantDetailQuery;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantDetailRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantDetailRepositoryImpl implements RestaurantDetailRepository {

    private final RestaurantDetailQuery restaurantDetailQuery;

    @Override
    public RestaurantDetail getRestaurantDetail(Integer restaurantId, Long userId) {
        if (isNull(restaurantId)) {
            throw new IllegalArgumentException("restaurantId is null");
        }

        Long favoriteCount = restaurantDetailQuery.getFavoriteCount(restaurantId);

        Optional<RestaurantDetail> optional = restaurantDetailQuery.getRestaurantDetails(
                restaurantId, userId, favoriteCount);

        if (optional.isEmpty()) {
            throw new DataNotFoundException(ErrorCode.RESTAURANT_NOT_FOUND, restaurantId, "식당");
        }

        return optional.get();
    }
}
