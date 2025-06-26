package com.kustaurant.kustaurant.restaurant.application.service.query;

import com.kustaurant.kustaurant.restaurant.application.service.query.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.restaurant.infrastructure.spec.RestaurantChartSpec;
import com.kustaurant.kustaurant.restaurant.application.service.query.port.RestaurantQueryRepository;
import com.kustaurant.kustaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.application.service.command.RestaurantFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantHomeService {

    private final int TOP_RESTAURANT_SIZE = 16;
    private final int RECOMMENDATION_SIZE = 15;

    private final RestaurantQueryRepository restaurantQueryRepository;
    private final RestaurantFavoriteService restaurantFavoriteService;

    private Random rand = new Random();

    public List<RestaurantTierDTO> getTopRestaurants() {

        List<RestaurantTierDTO> dtoList = restaurantQueryRepository.findAll(
                        RestaurantChartSpec.withCuisinesAndLocationsAndSituations(null, null, null, "ACTIVE", null, true))
                .stream().map(RestaurantQueryMapper::toDto).toList();

        return dtoList.subList(0, Math.min(dtoList.size(), TOP_RESTAURANT_SIZE));
    }


    public List<RestaurantTierDTO> getRecommendedOrRandomRestaurants(Long userId) {
        List<RestaurantTierDTO> dtoList = new ArrayList<>(restaurantQueryRepository.findAll(
                        RestaurantChartSpec.withCuisinesAndLocationsAndSituations(null, null, null, "ACTIVE", null, true))
                .stream().map(RestaurantQueryMapper::toDto).toList());
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
        return restaurantQueryRepository.findAll(
                        RestaurantChartSpec.withCuisinesAndLocationsAndSituations(List.of(favoriteCuisine), null, null, "ACTIVE", null, true))
                .stream().map(RestaurantQueryMapper::toDto).toList()
                .subList(0, Math.min(dtoList.size(), RECOMMENDATION_SIZE));
    }

    public String getFavoriteCuisine(Long userId) {
        // 1. 즐겨찾기한 식당을 가져옵니다.
        List<Restaurant> favoriteRestaurants = restaurantFavoriteService.getFavoriteRestaurantDtoList(userId);

        // 2. 식당들의 카테고리를 수집하여 가장 많이 즐겨찾기한 카테고리를 찾습니다.
        Map<String, Long> cuisineFrequencyMap = favoriteRestaurants.stream()
                .collect(Collectors.groupingBy(Restaurant::getRestaurantCuisine, Collectors.counting()));

        // 3. 가장 많이 즐겨찾기된 카테고리 찾기
        return Collections.max(cuisineFrequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}
