package com.kustaurant.kustaurant.restaurant.home;

import com.kustaurant.kustaurant.restaurant.tier.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.restaurant.tier.service.port.RestaurantChartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RestaurantHomeService {

    private final int TOP_RESTAURANT_SIZE = 16;
    private final int RECOMMENDATION_SIZE = 15;

    private final RestaurantChartRepository restaurantChartRepository;

    private Random rand = new Random();

    public List<RestaurantTierDTO> getTopRestaurants() {

//        List<RestaurantTierDTO> dtoList = restaurantChartRepository
//                .findAll(
//                        RestaurantChartSpec.withCuisinesAndLocationsAndSituations(null, null, null, "ACTIVE", null, true))
//                .stream().map(RestaurantQueryMapper::toDto).toList();
        List<RestaurantTierDTO> dtoList = new ArrayList<>();

        return dtoList.subList(0, Math.min(dtoList.size(), TOP_RESTAURANT_SIZE));
    }


    public List<RestaurantTierDTO> getRecommendedOrRandomRestaurants(Long userId) {
//        List<RestaurantTierDTO> dtoList = new ArrayList<>(restaurantChartRepository.findAll(
//                        RestaurantChartSpec.withCuisinesAndLocationsAndSituations(null, null, null, "ACTIVE", null, true))
//                .stream().map(RestaurantQueryMapper::toDto).toList());
//        Collections.shuffle(dtoList, rand);
        List<RestaurantTierDTO> dtoList = new ArrayList<>();

        // 미로그인 시: 랜덤으로 15개의 식당 반환
        if (userId == null) {
            return dtoList.subList(0, Math.min(dtoList.size(), RECOMMENDATION_SIZE));
        }

        // 로그인 시: 즐겨찾기 기반 추천 식당 반환
//        String favoriteCuisine = getFavoriteCuisine(userId);
        String favoriteCuisine = null;
        if (favoriteCuisine == null) {
            // 즐찾이 없는 경우
            return dtoList.subList(0, Math.min(dtoList.size(), RECOMMENDATION_SIZE));
        }
        // 즐찾이 있는 경우
        return new ArrayList<>();
//        return restaurantChartRepository.findAll(
//                        RestaurantChartSpec.withCuisinesAndLocationsAndSituations(List.of(favoriteCuisine), null, null, "ACTIVE", null, true))
//                .stream().map(RestaurantQueryMapper::toDto).toList()
//                .subList(0, Math.min(dtoList.size(), RECOMMENDATION_SIZE));
    }

//    public String getFavoriteCuisine(Long userId) {
//        // 1. 즐겨찾기한 식당을 가져옵니다.
//        List<Integer> favoriteRestaurants = restaurantFavoriteService.getFavoriteRestaurantIdList(userId);
//
//        // 2. 식당들의 카테고리를 수집하여 가장 많이 즐겨찾기한 카테고리를 찾습니다.
//        Map<String, Long> cuisineFrequencyMap = favoriteRestaurants.stream()
//                .map(restaurantRepository::getById)
//                .filter(r -> Objects.equals(r.getStatus(), "ACTIVE"))
//                .collect(Collectors.groupingBy(Restaurant::getRestaurantCuisine, Collectors.counting()));
//
//        // 3. 가장 많이 즐겨찾기된 카테고리 찾기
//        return Collections.max(cuisineFrequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
//    }
}
