package com.kustaurant.kustaurant.restaurant.search.service;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.RestaurantSearchEngine;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.response.SearchResult;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.RestaurantSearchRepository;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForSearch;
import com.kustaurant.kustaurant.restaurant.search.service.response.RestaurantSearchResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestaurantSearchV3Service {

    private final RestaurantSearchEngine searchEngine;
    private final RestaurantSearchRepository restaurantSearchRepository;

    public RestaurantSearchResponse search(String[] kwArr, @Nullable Long userId, Pageable pageable) {
        if (kwArr == null || kwArr.length == 0) {return new RestaurantSearchResponse(List.of(), false);}

        SearchResult searchResult = searchEngine.searchRestaurantIds(kwArr, pageable);

        Map<Long, RestaurantForSearch> restaurantInfos
                = restaurantSearchRepository.getRestaurantForSearch(searchResult.ids.getContent(), userId);

        return new RestaurantSearchResponse(searchResult, restaurantInfos);
    }
}
