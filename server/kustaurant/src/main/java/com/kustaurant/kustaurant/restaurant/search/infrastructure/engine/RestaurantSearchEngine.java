package com.kustaurant.kustaurant.restaurant.search.infrastructure.engine;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.response.SearchResult;
import org.springframework.data.domain.Pageable;

public interface RestaurantSearchEngine {

    SearchResult searchRestaurantIds(String[] kwArr, Pageable pageable);
}
