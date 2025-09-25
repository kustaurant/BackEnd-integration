package com.kustaurant.kustaurant.restaurant.restaurant.service;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantDetailRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantDetailRepository restaurantDetailRepository;

    public Restaurant getActiveDomain(Long restaurantId) {
        return restaurantRepository.getByIdAndStatus(restaurantId, "ACTIVE");
    }

    public RestaurantDetail getRestaurantDetail(Long restaurantId, Long userId) {
        return restaurantDetailRepository.getRestaurantDetail(restaurantId, userId);
    }
}
