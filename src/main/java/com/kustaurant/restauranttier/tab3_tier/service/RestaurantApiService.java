package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.common.exception.exception.OptionalNotExistException;
import com.kustaurant.restauranttier.common.exception.exception.ParamException;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantFavorite;
import com.kustaurant.restauranttier.tab3_tier.etc.CuisineEnum;
import com.kustaurant.restauranttier.tab3_tier.etc.LocationEnum;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantApiRepository;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantFavoriteRepository;
import com.kustaurant.restauranttier.tab3_tier.specification.RestaurantSpecification;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantApiService {
    private final RestaurantApiRepository restaurantApiRepository;
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;
    private final UserRepository userRepository;


    public static final Integer evaluationCount = 2;

    public Restaurant findRestaurantById(Integer restaurantId) {
        Optional<Restaurant> restaurantOptional = restaurantApiRepository.findByRestaurantIdAndStatus(restaurantId, "ACTIVE");
        if (restaurantOptional.isEmpty()) {
            throw new OptionalNotExistException(restaurantId + " 식당이 없습니다.");
        }
        return restaurantOptional.get();
    }

    public List<Restaurant> getTopRestaurants() {
        // 모든 'ACTIVE' 상태의 식당을 불러온다.
        List<Restaurant> restaurants = restaurantApiRepository.findByStatus("ACTIVE");

        // 점수가 높은 식당 16개를 선택
        return restaurants.stream()
                .filter(r -> r.getRestaurantEvaluationCount() >= evaluationCount)
                .sorted(Comparator.comparingDouble(Restaurant::calculateAverageScore).reversed())
                .limit(16)
                .collect(Collectors.toList());
    }

    // 뽑기 리스트 반환
    public List<Restaurant> getRestaurantListByRandomPick(String cuisine, String location) {
        // cuisine 이 전체일 떄
        if(cuisine.equals("전체")){
            if(location.equals("전체")) {
                return restaurantApiRepository.findByStatus("ACTIVE");
            }
            return restaurantApiRepository.findByStatusAndRestaurantPosition("ACTIVE", location);
        }
        // cuisine이 전체가 아닐때

        else{
            if(location.equals("전체")) {
                return restaurantApiRepository.findByRestaurantCuisineAndStatus(cuisine, "ACTIVE");
            }
            return restaurantApiRepository.findByRestaurantCuisineAndStatusAndRestaurantPosition(cuisine, "ACTIVE", location);
        }

    }

    public List<Restaurant> getRestaurantsByCuisinesAndLocations(String cuisines, String locations, Integer tierInfo, boolean isOrderByScore) {
        List<String> cuisineList;
        List<String> locationList;
        try {
            cuisineList = cuisines.contains("ALL") ? null : Arrays.stream(cuisines.split(",")).map(c -> CuisineEnum.valueOf(c).getValue()).toList();
        } catch (IllegalArgumentException e) {
            throw new ParamException("cuisines 파라미터 입력이 올바르지 않습니다.");
        }
        try {
            locationList = locations.contains("ALL") ? null : Arrays.stream(locations.split(",")).map(l -> LocationEnum.valueOf(l).getValue()).toList();
        } catch (IllegalArgumentException e) {
            throw new ParamException("locations 파라미터 입력이 올바르지 않습니다.");
        }

        if (cuisineList != null && cuisineList.contains("JH")) {
            cuisineList = List.of("JH");
        }

        return restaurantApiRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisineList, locationList, null, "ACTIVE", tierInfo, isOrderByScore));
    }

    public Page<Restaurant> getRestaurantsByCuisinesAndLocationsAndSituationsWithPage(
            String cuisines,
            String locations,
            String situations,
            Integer tierInfo,
            boolean isOrderByScore,
            int page,
            int size
    ) {
        List<String> cuisineList;
        List<String> locationList;
        List<Integer> situationList;
        try {
            cuisineList = cuisines.contains("ALL") ? null : Arrays.stream(cuisines.split(",")).map(c -> CuisineEnum.valueOf(c.trim()).getValue()).toList();
        } catch (IllegalArgumentException e) {
            throw new ParamException("cuisines 파라미터 입력이 올바르지 않습니다.");
        }
        try {
            locationList = locations.contains("ALL") ? null : Arrays.stream(locations.split(",")).map(l -> LocationEnum.valueOf(l.trim()).getValue()).toList();
        } catch (IllegalArgumentException e) {
            throw new ParamException("locations 파라미터 입력이 올바르지 않습니다.");
        }
        try {
            situationList = situations.contains("ALL") ? null : Arrays.stream(situations.split(",")).map(s -> Integer.parseInt(s.trim())).toList();
        } catch (IllegalArgumentException e) {
            throw new ParamException("situations 파라미터 입력이 올바르지 않습니다.");
        }

        if (cuisineList != null && cuisineList.contains("제휴업체")) {
            cuisineList = List.of("JH");
        }

        Pageable pageable = PageRequest.of(page, size);

        return restaurantApiRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisineList, locationList, situationList, "ACTIVE", tierInfo, isOrderByScore), pageable);
    }

    public boolean isSituationContainRestaurant(List<Integer> situationList, Restaurant restaurant) {
        // TODO: 여기서 상황 기준 설정
        return restaurant.getRestaurantSituationRelationList().stream()
                .anyMatch(el -> situationList.contains(el.getSituation().getSituationId()) && el.getDataCount() >= 3);
    }

    // 해당 식당을 해당 유저가 평가 했는가?
    public boolean isEvaluated(Restaurant restaurant, User user) {
        return user.getEvaluationList().stream()
                .anyMatch(evaluation -> evaluation.getRestaurant().equals(restaurant));
    }

    // 해당 식당을 해당 유저가 즐겨찾기 했는가?
    public boolean isFavorite(Restaurant restaurant, User user) {
        return user.getRestaurantFavoriteList().stream()
                .anyMatch(restaurantFavorite -> restaurantFavorite.getRestaurant().equals(restaurant));
    }

    public List<Restaurant> getRecommendedRestaurantsForUser(Integer userId) {
        // 1. 사용자의 정보를 가져옵니다.
        User user = userRepository.findByUserId(userId).orElse(null);

        // 2. 사용자의 즐겨찾기 목록을 가져옵니다.
        List<RestaurantFavorite> favorites = restaurantFavoriteRepository.findByUser(user);

        if (favorites.isEmpty()) {
            // 즐겨찾기한 식당이 없을 경우 랜덤 식당 15개 추천
            List<Restaurant> restaurants = getRestaurantsByCuisinesAndLocations("ALL", "ALL", null, false);
            Collections.shuffle(restaurants, new Random());
            System.out.println("랜덤추천");

            return restaurants.stream().limit(15).collect(Collectors.toList());

        }

        // 3. 식당들의 카테고리를 수집하여 가장 많이 즐겨찾기한 카테고리를 찾습니다.
        Map<String, Long> cuisineFrequencyMap = favorites.stream()
                .collect(Collectors.groupingBy(fav -> fav.getRestaurant().getRestaurantCuisine(), Collectors.counting()));

        // 가장 많이 즐겨찾기된 카테고리 찾기
        String favoriteCuisine = Collections.max(cuisineFrequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println(favoriteCuisine + " : 가장 많이 즐찾된 카테고리");
        // 4. 해당 카테고리와 일치하는 식당을 검색합니다.
        List<Restaurant> recommendedRestaurants = restaurantApiRepository.findByRestaurantCuisineAndStatus(favoriteCuisine, "ACTIVE");

        // 5. 평점이 높은 순으로 정렬합니다.
        recommendedRestaurants.sort(Comparator.comparingDouble(Restaurant::calculateAverageScore).reversed());

        // 6. 원하는 개수만큼 자릅니다 (예: 15개).
        return recommendedRestaurants.stream().limit(15).collect(Collectors.toList());
    }
    public List<Restaurant> getRecommendedOrRandomRestaurants(Integer userId) {
        if (userId == null) {
            System.out.println("미로그인");
            // 미로그인 시: 랜덤으로 15개의 식당 반환
            List<Restaurant> restaurants = getRestaurantsByCuisinesAndLocations("ALL", "ALL", null, false);
            Collections.shuffle(restaurants, new Random());
            return restaurants.stream().limit(15).collect(Collectors.toList());
        } else {
            System.out.println("로그인");

            // 로그인 시: 즐겨찾기 기반 추천 식당 반환
            return getRecommendedRestaurantsForUser(userId);
        }
    }
}
