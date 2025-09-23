package com.kustaurant.mainapp.restaurant.query.home;

import com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.mainapp.restaurant.query.common.infrastructure.repository.RestaurantCoreInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RestaurantHomeService {

    private final int TOP_RESTAURANT_SIZE = 16;
    private final int RECOMMENDATION_SIZE = 15;

    private final RestaurantHomeRepository restaurantHomeRepository;
    private final RestaurantCoreInfoRepository restaurantCoreInfoRepository;

    public List<RestaurantCoreInfoDto> getTopRestaurants(Long userId) {
        // 식당 id만 읽어오기
        List<Long> ids = restaurantHomeRepository.getTopRestaurantIds(TOP_RESTAURANT_SIZE);
        // 식당 데이터(+ 평가 여부, 즐찾 여부, 상황 리스트) 가져오기
        return restaurantCoreInfoRepository.getRestaurantTiers(ids, userId);
    }


    public List<RestaurantCoreInfoDto> getRecommendedOrRandomRestaurants(Long userId) {
        // 식당 id만 읽어오기
        List<Long> ids = restaurantHomeRepository.getRandomRestaurantIds(RECOMMENDATION_SIZE);
        // 식당 데이터(+ 평가 여부, 즐찾 여부, 상황 리스트) 가져오기
        return restaurantCoreInfoRepository.getRestaurantTiers(ids, userId);
    }
}
