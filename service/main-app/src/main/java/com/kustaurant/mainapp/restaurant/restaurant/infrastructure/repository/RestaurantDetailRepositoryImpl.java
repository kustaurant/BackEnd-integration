package com.kustaurant.mainapp.restaurant.restaurant.infrastructure.repository;

import static java.util.Objects.isNull;

import com.kustaurant.mainapp.global.exception.ErrorCode;
import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
import com.kustaurant.mainapp.restaurant.restaurant.infrastructure.repository.query.RestaurantDetailQuery;
import com.kustaurant.mainapp.restaurant.restaurant.service.dto.RestaurantDetail;
import com.kustaurant.mainapp.restaurant.restaurant.service.port.RestaurantDetailRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantDetailRepositoryImpl implements RestaurantDetailRepository {

    private final RestaurantDetailQuery restaurantDetailQuery;

    @Override
    public RestaurantDetail getRestaurantDetail(Long restaurantId, Long userId) {
        if (isNull(restaurantId)) {
            throw new IllegalArgumentException("restaurantId is null");
        }

        Optional<RestaurantDetail> optional = restaurantDetailQuery.getRestaurantDetails(
                restaurantId, userId);

        if (optional.isEmpty()) {
            throw new DataNotFoundException(ErrorCode.RESTAURANT_NOT_FOUND, restaurantId, "식당");
        }

        return optional.get();
    }
}
