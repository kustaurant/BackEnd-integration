package com.kustaurant.kustaurant.restaurant.query.home;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import java.util.List;

public interface RestaurantHomeRepository {

    List<RestaurantCoreInfoDto> getTopRestaurants(int size, Long userId);

    List<RestaurantCoreInfoDto> getRandomRestaurants(int size, Long userId);
}
