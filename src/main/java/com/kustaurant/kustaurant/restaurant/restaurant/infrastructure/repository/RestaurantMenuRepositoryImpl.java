package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantMenu;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantMenuEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RestaurantMenuRepositoryImpl implements RestaurantMenuRepository {

    private final RestaurantJpaMenuRepository jpaRepository;

    @Override
    public List<RestaurantMenu> findByRestaurantOrderById(Integer restaurantId) {
        return jpaRepository.findByRestaurantIdOrderById(restaurantId)
                .stream()
                .map(RestaurantMenuEntity::toModel)
                .toList();
    }
}
