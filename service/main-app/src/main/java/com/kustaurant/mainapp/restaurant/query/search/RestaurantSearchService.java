package com.kustaurant.mainapp.restaurant.query.search;

import com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.mainapp.restaurant.query.common.infrastructure.repository.RestaurantCoreInfoRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantSearchService {

    private final int SEARCH_MAX_SIZE = 50;

    private final RestaurantSearchRepository restaurantSearchRepository;
    private final RestaurantCoreInfoRepository restaurantCoreInfoRepository;

    public List<RestaurantCoreInfoDto> search(String[] kwArr, @Nullable Long userId) {
        if (kwArr == null || kwArr.length == 0) {
            return List.of();
        }
        // 식당 id만 읽어오기
        List<Long> ids = restaurantSearchRepository.searchRestaurantIds(kwArr, SEARCH_MAX_SIZE);
        // 식당 데이터(+ 평가 여부, 즐찾 여부, 상황 리스트) 가져오기
        return restaurantCoreInfoRepository.getRestaurantTiers(ids, userId);
    }
}
