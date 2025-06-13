package com.kustaurant.kustaurant.restaurant.application.service.command;

import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.application.service.command.port.RestaurantRepository;
import com.kustaurant.kustaurant.global.exception.exception.OptionalNotExistException;
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
            throw new OptionalNotExistException(restaurantId + " 식당이 없습니다.");
        }
        return restaurantOptional.get();
    }

    @Transactional
    public void saveRestaurant(RestaurantEntity restaurant) {
        restaurantRepository.save(restaurant);
    }
}
