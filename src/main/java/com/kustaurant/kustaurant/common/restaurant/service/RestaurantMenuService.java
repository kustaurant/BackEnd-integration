package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantMenu;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantMenuService {

    private final RestaurantMenuRepository restaurantMenuRepository;

    public List<RestaurantMenu> findMenusByRestaurantId(Integer restaurantId) {
        return restaurantMenuRepository.findByRestaurantOrderById(restaurantId);
    }
}
