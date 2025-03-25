package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.global.exception.exception.OptionalNotExistException;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.RestaurantSpecification;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantApiService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;
    private final UserRepository userRepository;


    public static final Integer evaluationCount = 2;

    public RestaurantEntity findRestaurantById(Integer restaurantId) {
        Optional<RestaurantEntity> restaurantOptional = restaurantRepository.findByRestaurantIdAndStatus(restaurantId, "ACTIVE");
        if (restaurantOptional.isEmpty()) {
            throw new OptionalNotExistException(restaurantId + " 식당이 없습니다.");
        }
        return restaurantOptional.get();
    }

    @Transactional
    public void saveRestaurant(RestaurantEntity restaurant) {
        restaurantRepository.save(restaurant);
    }

    public List<RestaurantTierDTO> getTopRestaurants() {
        // 모든 'ACTIVE' 상태의 식당을 불러온다.
        List<RestaurantEntity> restaurants = restaurantRepository.findByStatus("ACTIVE");
        restaurants = restaurants.stream()
                .filter(r -> r.getRestaurantEvaluationCount() >= evaluationCount)
                .sorted(Comparator.comparingDouble(RestaurantEntity::calculateAverageScore).reversed())
                .limit(16)
                .toList();
        List<RestaurantTierDTO> topRestaurantsByRatingDTOs = restaurants.stream()
                .map(restaurant -> RestaurantTierDTO.convertRestaurantToTierDTO(restaurant, null, null, null))
                .collect(Collectors.toList());
        return topRestaurantsByRatingDTOs;
    }

    // 뽑기 리스트 반환
    public List<RestaurantEntity> getRestaurantListByRandomPick(String cuisine, String location) {
        // cuisine 이 전체일 떄
        if(cuisine.equals("전체")){
            if(location.equals("전체")) {
                return restaurantRepository.findByStatus("ACTIVE");
            }
            return restaurantRepository.findByStatusAndRestaurantPosition("ACTIVE", location);
        }
        // cuisine이 전체가 아닐때

        else{
            if(location.equals("전체")) {
                return restaurantRepository.findByRestaurantCuisineAndStatus(cuisine, "ACTIVE");
            }
            return restaurantRepository.findByRestaurantCuisineAndStatusAndRestaurantPosition(cuisine, "ACTIVE", location);
        }

    }

    public List<RestaurantEntity> getRestaurantsByCuisinesAndSituationsAndLocations(
            List<String> cuisineList, List<Integer> situationList, List<String> locationList,
            Integer tierInfo, boolean isOrderByScore
    ) {
        return restaurantRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisineList, locationList, situationList, "ACTIVE", tierInfo, isOrderByScore));
    }

    // 해당 식당을 해당 유저가 평가 했는가?
    public boolean isEvaluated(RestaurantEntity restaurant, User user) {
        if (user == null || restaurant == null) {
            return false;
        }
        return user.getEvaluationList().stream()
                .anyMatch(evaluation -> evaluation.getRestaurant().equals(restaurant));
    }

    // 해당 식당을 해당 유저가 즐겨찾기 했는가?
    public boolean isFavorite(RestaurantEntity restaurant, User user) {
        if (user == null || restaurant == null) {
            return false;
        }
        return user.getRestaurantFavoriteList().stream()
                .anyMatch(restaurantFavorite -> restaurantFavorite.getRestaurant().equals(restaurant));
    }

    public List<RestaurantEntity> getRecommendedRestaurantsForUser(Integer userId) {
        // 1. 사용자의 정보를 가져옵니다.
        User user = userRepository.findByUserId(userId).orElse(null);

        // 2. 사용자의 즐겨찾기 목록을 가져옵니다.
        List<RestaurantFavoriteEntity> favorites = restaurantFavoriteRepository.findByUser(user);

        if (favorites.isEmpty()) {
            // 즐겨찾기한 식당이 없을 경우 랜덤 식당 15개 추천
            List<RestaurantEntity> restaurants = restaurantRepository.findByStatus("ACTIVE");
            Collections.shuffle(restaurants, new Random());

            return restaurants.stream().limit(15).collect(Collectors.toList());

        }

        // 3. 식당들의 카테고리를 수집하여 가장 많이 즐겨찾기한 카테고리를 찾습니다.
        Map<String, Long> cuisineFrequencyMap = favorites.stream()
                .collect(Collectors.groupingBy(fav -> fav.getRestaurant().getRestaurantCuisine(), Collectors.counting()));

        // 가장 많이 즐겨찾기된 카테고리 찾기
        String favoriteCuisine = Collections.max(cuisineFrequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        // 4. 해당 카테고리와 일치하는 식당을 검색합니다.
        List<RestaurantEntity> recommendedRestaurants = restaurantRepository.findByRestaurantCuisineAndStatus(favoriteCuisine, "ACTIVE");

        // 5. 평점이 높은 순으로 정렬합니다.
        recommendedRestaurants.sort(Comparator.comparingDouble(RestaurantEntity::calculateAverageScore).reversed());

        // 6. 원하는 개수만큼 자릅니다 (예: 15개).
        return recommendedRestaurants.stream().limit(15).collect(Collectors.toList());
    }
    public List<RestaurantTierDTO> getRecommendedOrRandomRestaurants(Integer userId) {
        List<RestaurantEntity> restaurants;
        if (userId == null) {
            // 미로그인 시: 랜덤으로 15개의 식당 반환
            restaurants = restaurantRepository.findByStatus("ACTIVE");
            Collections.shuffle(restaurants, new Random());
            restaurants=  restaurants.stream().limit(15).collect(Collectors.toList());
        } else {
            // 로그인 시: 즐겨찾기 기반 추천 식당 반환
            restaurants= getRecommendedRestaurantsForUser(userId);
        }

        List<RestaurantTierDTO> restaurantsForMeDTOs = restaurants.stream()
                .map(restaurant -> RestaurantTierDTO.convertRestaurantToTierDTO(restaurant, null, null, null))
                .collect(Collectors.toList());

        return restaurantsForMeDTOs;
    }
}
