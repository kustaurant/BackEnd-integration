package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.common.exception.exception.OptionalNotExistException;
import com.kustaurant.restauranttier.common.exception.exception.ParamException;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.etc.CuisineEnum;
import com.kustaurant.restauranttier.tab3_tier.etc.LocationEnum;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantApiRepository;
import com.kustaurant.restauranttier.tab3_tier.specification.RestaurantSpecification;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantApiService {
    private final RestaurantApiRepository restaurantApiRepository;

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
}
