package com.kustaurant.kustaurant.restaurant.restaurant.service;

import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationQueryService;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantMenu;
import com.kustaurant.kustaurant.restaurant.restaurant.controller.response.RestaurantDetailDTO;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantService {
    // repository
    private final RestaurantRepository restaurantRepository;
    // service
    private final RestaurantFavoriteService restaurantFavoriteService;
    private final RestaurantMenuService restaurantMenuService;
    private final EvaluationQueryService evaluationQueryService;

    public Restaurant getActiveDomain(Integer restaurantId) {
        return restaurantRepository.getByIdAndStatus(restaurantId, "ACTIVE");
    }

    public RestaurantDetailDTO getActiveRestaurantDetailDto(Integer restaurantId, Long userId) {
        Restaurant restaurant = restaurantRepository.getByIdAndStatus(restaurantId, "ACTIVE");
        List<RestaurantMenu> menus = restaurantMenuService.findMenusByRestaurantId(restaurantId);

        boolean isEvaluated = evaluationQueryService.isUserEvaluated(userId, restaurantId);
        boolean isFavorite = restaurantFavoriteService.isUserFavorite(userId, restaurantId);

        return RestaurantDetailDTO.from(restaurant, menus, isEvaluated, isFavorite);
    }

    @Transactional
    public void afterEvaluationCreated(Integer restaurantId, Double score) {
        Restaurant restaurant = restaurantRepository.getById(restaurantId);

        restaurant.afterEvaluationCreated(score);

        restaurantRepository.updateStatistics(restaurant);
    }

    @Transactional
    public void afterReEvaluated(Integer restaurantId, Double preScore, Double postScore) {
        Restaurant restaurant = restaurantRepository.getById(restaurantId);

        restaurant.afterReEvaluated(preScore, postScore);

        restaurantRepository.updateStatistics(restaurant);
    }
}
