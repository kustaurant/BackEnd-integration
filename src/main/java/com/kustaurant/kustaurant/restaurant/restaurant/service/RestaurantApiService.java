package com.kustaurant.kustaurant.restaurant.restaurant.service;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantApiService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantEntity findRestaurantById(Integer restaurantId) {
        Optional<RestaurantEntity> restaurantOptional = restaurantRepository.findByRestaurantIdAndStatus(restaurantId, "ACTIVE");
        if (restaurantOptional.isEmpty()) {
            throw new DataNotFoundException(RESTAURANT_NOT_FOUND, restaurantId, "식당");
        }
        return restaurantOptional.get();
    }

    @Transactional
    public void saveRestaurant(RestaurantEntity restaurant) {
        restaurantRepository.save(restaurant);
    }
}
