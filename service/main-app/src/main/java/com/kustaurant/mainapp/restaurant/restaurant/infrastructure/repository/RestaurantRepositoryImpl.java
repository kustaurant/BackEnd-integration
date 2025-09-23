package com.kustaurant.mainapp.restaurant.restaurant.infrastructure.repository;

import static com.kustaurant.mainapp.global.exception.ErrorCode.*;

import com.kustaurant.mainapp.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.mainapp.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.mainapp.restaurant.restaurant.service.port.RestaurantRepository;
import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository jpa;

    @Override
    public Restaurant getByIdAndStatus(Long id, String status) {
        return jpa.findByRestaurantIdAndStatus(id, status).map(RestaurantEntity::toModel)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_NOT_FOUND, "요청한 restaurant가 존재하지 않습니다. 요청 정보 - id: " + id + ", status: " + status));
    }

    @Override
    public void increaseVisitCount(Long restaurantId) {
        jpa.incrementViews(restaurantId);
    }
}
