package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.RestaurantSpecification;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantTierService {

    private final RestaurantRepository restaurantRepository;

    // Cuisine, Situation, Location 조건에 맞는 식당 리스트를 반환
    public List<RestaurantEntity> findByConditions(
            List<String> cuisineList, List<Integer> situationList, List<String> locationList,
            Integer tierInfo, boolean isOrderByScore
    ) {
        return restaurantRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisineList, locationList, situationList, "ACTIVE", tierInfo, isOrderByScore));
    }

    // 위 함수의 페이징 버전
    public Page<RestaurantEntity> findByConditionsWithPage(
            List<String> cuisineList, List<Integer> situationList, List<String> locationList,
            Integer tierInfo, boolean isOrderByScore, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return restaurantRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisineList, locationList, situationList, "ACTIVE", tierInfo, isOrderByScore), pageable);
    }
}
