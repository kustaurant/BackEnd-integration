package com.kustaurant.kustaurant.restaurant.restaurant.service;

import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantMenu;
import com.kustaurant.kustaurant.restaurant.restaurant.controller.response.RestaurantDetailDTO;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    // repository
    private final RestaurantRepository restaurantRepository;
    // service
    private final RestaurantFavoriteService restaurantFavoriteService;
    private final RestaurantMenuService restaurantMenuService;
    private final EvaluationService evaluationService;

    public Restaurant getActiveDomain(Integer restaurantId) {
        return restaurantRepository.getByIdAndStatus(restaurantId, "ACTIVE");
    }

    public RestaurantDetailDTO getActiveRestaurantDetailDto(Integer restaurantId, Long userId) {
        Restaurant restaurant = restaurantRepository.getByIdAndStatus(restaurantId, "ACTIVE");
        List<RestaurantMenu> menus = restaurantMenuService.findMenusByRestaurantId(restaurantId);

        boolean isEvaluated = evaluationService.isUserEvaluated(userId, restaurantId);
        boolean isFavorite = restaurantFavoriteService.isUserFavorite(userId, restaurantId);

        return RestaurantDetailDTO.from(restaurant, menus, isEvaluated, isFavorite);
    }
}
