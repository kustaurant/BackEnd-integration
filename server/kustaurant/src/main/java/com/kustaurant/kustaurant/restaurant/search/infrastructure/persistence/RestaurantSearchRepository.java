package com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForEngine;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForSearch;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface RestaurantSearchRepository {

    List<Long> searchRestaurantIds(String[] kwArr, int size);

    List<RestaurantForEngine> getRestaurantForEngine();

    Map<Long, RestaurantForSearch> getRestaurantForSearch(List<Long> restaurantIds, @Nullable Long userId);
}
