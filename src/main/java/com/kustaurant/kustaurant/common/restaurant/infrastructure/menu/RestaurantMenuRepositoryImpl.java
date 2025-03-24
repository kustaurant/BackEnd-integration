package com.kustaurant.kustaurant.common.restaurant.infrastructure.menu;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantMenuDomain;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RestaurantMenuRepositoryImpl implements RestaurantMenuRepository {

    private final RestaurantJpaMenuRepository jpaRepository;

    @Override
    public List<RestaurantMenuDomain> findByRestaurantOrderById(Integer restaurantId) {
        return jpaRepository.findByRestaurant_RestaurantIdOrderByMenuId(restaurantId)
                .stream()
                .map(RestaurantMenuEntity::toModel)
                .toList();
    }
}
