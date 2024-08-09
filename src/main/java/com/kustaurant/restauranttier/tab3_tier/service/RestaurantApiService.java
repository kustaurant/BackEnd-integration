package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.etc.CuisineEnum;
import com.kustaurant.restauranttier.tab3_tier.etc.LocationEnum;
import com.kustaurant.restauranttier.tab3_tier.etc.SituationEnum;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantApiRepository;
import com.kustaurant.restauranttier.tab3_tier.specification.RestaurantSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RestaurantApiService {
    private final RestaurantApiRepository restaurantApiRepository;

    public static final Integer evaluationCount = 2;

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
        // TODO: enum에 없는게 들어왔을 경우 예외 처리
        List<String> cuisineList = cuisines.contains("ALL") ? null : Arrays.stream(cuisines.split(",")).map(c -> CuisineEnum.valueOf(c).getValue()).toList();
        List<String> locationList = locations.contains("ALL") ? null : Arrays.stream(locations.split(",")).map(l -> LocationEnum.valueOf(l).getValue()).toList();

        if (cuisineList != null && cuisineList.contains("JH")) {
            cuisineList = List.of("JH");
        }

        return restaurantApiRepository.findAll(RestaurantSpecification.withCuisinesAndLocations(cuisineList, locationList, "ACTIVE", tierInfo, isOrderByScore));
    }

    public Page<Restaurant> getRestaurantsByCuisinesAndLocationsWithPage(String cuisines, String locations, Integer tierInfo, boolean isOrderByScore, int page, int size) {
        List<String> cuisineList = cuisines.contains("ALL") ? null : Arrays.stream(cuisines.split(",")).map(c -> CuisineEnum.valueOf(c).getValue()).toList();
        List<String> locationList = locations.contains("ALL") ? null : Arrays.stream(locations.split(",")).map(l -> LocationEnum.valueOf(l).getValue()).toList();

        if (cuisineList != null && cuisineList.contains("JH")) {
            cuisineList = List.of("JH");
        }

        Pageable pageable = PageRequest.of(page, size);

        return restaurantApiRepository.findAll(RestaurantSpecification.withCuisinesAndLocations(cuisineList, locationList, "ACTIVE", tierInfo, isOrderByScore), pageable);
    }

    public boolean isSituationContainRestaurant(List<Integer> situationList, Restaurant restaurant) {
        // TODO: 여기서 상황 기준 설정
        return restaurant.getRestaurantSituationRelationList().stream()
                .anyMatch(el -> situationList.contains(el.getSituation().getSituationId()) && el.getDataCount() > 3);
    }
}
