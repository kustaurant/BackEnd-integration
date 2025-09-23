package com.kustaurant.mainapp.restaurant.restaurant.service;

import com.kustaurant.mainapp.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.mainapp.restaurant.restaurant.service.dto.RestaurantDetail;
import com.kustaurant.mainapp.restaurant.restaurant.service.port.RestaurantDetailRepository;
import com.kustaurant.mainapp.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

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
