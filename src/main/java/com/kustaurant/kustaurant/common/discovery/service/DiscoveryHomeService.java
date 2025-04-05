package com.kustaurant.kustaurant.common.discovery.service;

import com.kustaurant.kustaurant.common.discovery.domain.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.discovery.infrastructure.DiscoverySpec;
import com.kustaurant.kustaurant.common.discovery.service.port.DiscoveryRepository;
import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscoveryHomeService {

    private final int TOP_RESTAURANT_SIZE = 16;
    private final int RECOMMENDATION_SIZE = 15;

    private final DiscoveryRepository discoveryRepository;
    private final RestaurantFavoriteService restaurantFavoriteService;

    private Random rand = new Random();

    public List<RestaurantTierDTO> getTopRestaurants() {

        List<RestaurantTierDTO> dtoList = discoveryRepository.findAll(
                        DiscoverySpec.withCuisinesAndLocationsAndSituations(null, null, null, "ACTIVE", null, true))
                .stream().map(DiscoveryMapper::toDto).toList();

        return dtoList.subList(0, Math.min(dtoList.size(), TOP_RESTAURANT_SIZE));
    }


    public List<RestaurantTierDTO> getRecommendedOrRandomRestaurants(Integer userId) {
        List<RestaurantTierDTO> dtoList = new ArrayList<>(discoveryRepository.findAll(
                        DiscoverySpec.withCuisinesAndLocationsAndSituations(null, null, null, "ACTIVE", null, true))
                .stream().map(DiscoveryMapper::toDto).toList());
        Collections.shuffle(dtoList, rand);

        // 미로그인 시: 랜덤으로 15개의 식당 반환
        if (userId == null) {
            return dtoList.subList(0, Math.min(dtoList.size(), RECOMMENDATION_SIZE));
        }

        // 로그인 시: 즐겨찾기 기반 추천 식당 반환
        String favoriteCuisine = getFavoriteCuisine(userId);
        if (favoriteCuisine == null) {
            // 즐찾이 없는 경우
            return dtoList.subList(0, Math.min(dtoList.size(), RECOMMENDATION_SIZE));
        }
        // 즐찾이 있는 경우
        return discoveryRepository.findAll(
                        DiscoverySpec.withCuisinesAndLocationsAndSituations(List.of(favoriteCuisine), null, null, "ACTIVE", null, true))
                .stream().map(DiscoveryMapper::toDto).toList()
                .subList(0, Math.min(dtoList.size(), RECOMMENDATION_SIZE));
    }

    public String getFavoriteCuisine(Integer userId) {
        // 1. 즐겨찾기한 식당을 가져옵니다.
        List<Restaurant> favoriteRestaurants = restaurantFavoriteService.getFavoriteRestaurantDtoList(userId);

        // 2. 식당들의 카테고리를 수집하여 가장 많이 즐겨찾기한 카테고리를 찾습니다.
        Map<String, Long> cuisineFrequencyMap = favoriteRestaurants.stream()
                .collect(Collectors.groupingBy(Restaurant::getRestaurantCuisine, Collectors.counting()));

        // 3. 가장 많이 즐겨찾기된 카테고리 찾기
        return Collections.max(cuisineFrequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}
