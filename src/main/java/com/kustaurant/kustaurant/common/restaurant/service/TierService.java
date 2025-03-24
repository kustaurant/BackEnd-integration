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
public class TierService {

    private final RestaurantRepository restaurantRepository;

    public List<RestaurantEntity> getRestaurantsByCuisinesAndSituationsAndLocations(
            List<String> cuisineList, List<Integer> situationList, List<String> locationList,
            Integer tierInfo, boolean isOrderByScore
    ) {
        return restaurantRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisineList, locationList, situationList, "ACTIVE", tierInfo, isOrderByScore));
    }

    public Page<RestaurantEntity> getRestaurantsByCuisinesAndSituationsAndLocationsWithPage(
            List<String> cuisineList, List<Integer> situationList, List<String> locationList,
            Integer tierInfo, boolean isOrderByScore, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return restaurantRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisineList, locationList, situationList, "ACTIVE", tierInfo, isOrderByScore), pageable);
    }
}
