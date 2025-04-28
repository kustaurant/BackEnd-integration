package com.kustaurant.kustaurant.common.restaurant.application.service.command;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantMenu;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.port.RestaurantMenuRepository;
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
