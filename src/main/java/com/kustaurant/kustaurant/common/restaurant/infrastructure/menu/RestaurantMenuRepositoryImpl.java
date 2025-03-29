package com.kustaurant.kustaurant.common.restaurant.infrastructure.menu;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantMenu;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RestaurantMenuRepositoryImpl implements RestaurantMenuRepository {

    private final RestaurantJpaMenuRepository jpaRepository;

    @Override
    public List<RestaurantMenu> findByRestaurantOrderById(Integer restaurantId) {
        return jpaRepository.findByRestaurant_RestaurantIdOrderByMenuId(restaurantId)
                .stream()
                .map(RestaurantMenuEntity::toDomain)
                .toList();
    }
}
