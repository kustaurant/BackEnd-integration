package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface RestaurantDetailRepository {

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    RestaurantDetail getRestaurantDetail(Long restaurantId, Long userId);
}
