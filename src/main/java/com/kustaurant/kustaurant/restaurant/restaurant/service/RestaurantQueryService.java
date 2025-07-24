package com.kustaurant.kustaurant.restaurant.restaurant.service;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantDetailRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantDetailRepository restaurantDetailRepository;

    public Restaurant getActiveDomain(Integer restaurantId) {
        return restaurantRepository.getByIdAndStatus(restaurantId, "ACTIVE");
    }

    public RestaurantDetail getRestaurantDetail(Integer restaurantId, Long userId) {
        return restaurantDetailRepository.getRestaurantDetail(restaurantId, userId);
    }

}
