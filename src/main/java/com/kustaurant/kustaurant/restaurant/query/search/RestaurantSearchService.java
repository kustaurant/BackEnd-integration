package com.kustaurant.kustaurant.restaurant.query.search;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantSearchService {

    private final int SEARCH_MAX_SIZE = 50;

    private final RestaurantSearchRepository restaurantSearchRepository;

    public List<RestaurantCoreInfoDto> search(String[] kwArr, @Nullable Long userId) {
        return restaurantSearchRepository.search(kwArr, userId, SEARCH_MAX_SIZE);
    }
}
