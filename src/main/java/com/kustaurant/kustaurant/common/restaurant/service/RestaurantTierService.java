package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.RestaurantSpecification;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantJpaRepository;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantTierService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantFavoriteService restaurantFavoriteService;
    private final EvaluationService evaluationService;

    // Cuisine, Situation, Location 조건에 맞는 식당 리스트를 반환
    public List<RestaurantTierDTO> findByConditions(
            List<String> cuisineList, List<Integer> situationList, List<String> locationList,
            Integer tierInfo, boolean isOrderByScore, @Nullable Integer userId
    ) {
        List<Restaurant> restaurants = restaurantRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisineList, locationList, situationList, "ACTIVE", tierInfo, isOrderByScore));

        if (restaurants.isEmpty()) {
            return new ArrayList<>();
        }
        List<RestaurantTierDTO> responseList = new ArrayList<>();


        // 순위
        int ranking = 1;
        for (Restaurant restaurant : restaurants) {
            responseList.add(
                    RestaurantTierDTO.convertRestaurantToTierDTO(
                            restaurant,
                            ranking++,
                            evaluationService.isUserEvaluated(userId, restaurant.getRestaurantId()),
                            restaurantFavoriteService.isUserFavorite(userId, restaurant.getRestaurantId())
                    )
            );
        }

        return responseList;
    }

    // 위 함수의 페이징 버전
    public List<RestaurantTierDTO> findByConditionsWithPage(
            List<String> cuisineList, List<Integer> situationList, List<String> locationList,
            Integer tierInfo, boolean isOrderByScore, int page, int size, @Nullable Integer userId
    ) {
        Pageable pageable = PageRequest.of(page, size);

        List<Restaurant> restaurants = restaurantRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisineList, locationList, situationList, "ACTIVE", tierInfo, isOrderByScore), pageable);

        if (restaurants.isEmpty()) {
            return new ArrayList<>();
        }
        List<RestaurantTierDTO> responseList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            try {
                // 순위
                Integer ranking = null;
                Restaurant restaurant = restaurants.get(i);
                if (restaurant.getMainTier() > 0) {
                    ranking = page * size + i + 1;
                }
                responseList.add(
                        RestaurantTierDTO.convertRestaurantToTierDTO(
                                restaurant,
                                ranking,
                                evaluationService.isUserEvaluated(userId, restaurant.getRestaurantId()),
                                restaurantFavoriteService.isUserFavorite(userId, restaurant.getRestaurantId())
                        )
                );
            } catch (IndexOutOfBoundsException ignored) {

            }
        }

        return responseList;
    }
}
