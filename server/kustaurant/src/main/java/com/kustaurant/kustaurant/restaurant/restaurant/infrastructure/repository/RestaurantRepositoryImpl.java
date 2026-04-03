package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.mapper.RestaurantMapper;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository jpa;

    @Override
    public Restaurant getByIdAndStatus(Long id, String status) {
        return jpa.findByRestaurantIdAndStatus(id, status).map(RestaurantMapper::toModel)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_NOT_FOUND, "요청한 restaurant가 존재하지 않습니다. 요청 정보 - id: " + id + ", status: " + status));
    }

    @Override
    public void increaseVisitCount(Long restaurantId) {
        jpa.incrementViews(restaurantId);
    }
}
