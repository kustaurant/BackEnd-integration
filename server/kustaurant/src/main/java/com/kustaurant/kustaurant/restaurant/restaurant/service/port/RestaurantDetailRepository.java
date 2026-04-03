package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetailV2;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface RestaurantDetailRepository {

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    RestaurantDetail getRestaurantDetail(Long restaurantId, Long userId);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    RestaurantDetailV2 getRestaurantDetailV2(Long restaurantId, Long userId);
}
