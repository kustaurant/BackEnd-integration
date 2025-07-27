package com.kustaurant.kustaurant.restaurant.query.home;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RestaurantHomeService {

    private final int TOP_RESTAURANT_SIZE = 16;
    private final int RECOMMENDATION_SIZE = 15;

    private final RestaurantHomeRepository restaurantHomeRepository;

    public List<RestaurantCoreInfoDto> getTopRestaurants(Long userId) {
        return restaurantHomeRepository.getTopRestaurants(TOP_RESTAURANT_SIZE, userId);
    }


    public List<RestaurantCoreInfoDto> getRecommendedOrRandomRestaurants(Long userId) {
        return restaurantHomeRepository.getRandomRestaurants(RECOMMENDATION_SIZE, userId);
    }
}
