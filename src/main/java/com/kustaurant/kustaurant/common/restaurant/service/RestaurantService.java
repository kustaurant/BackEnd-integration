package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.common.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantMenu;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantDetailDTO;
import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantRepository;
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

    public Restaurant getDomain(Integer restaurantId) {
        return restaurantRepository.getById(restaurantId);
    }

    public RestaurantDetailDTO getRestaurantDetailDto(Integer restaurantId, Integer userId, String userAgent) {
        Restaurant restaurant = restaurantRepository.getById(restaurantId);
        List<RestaurantMenu> menus = restaurantMenuService.findMenusByRestaurantId(restaurantId);

        boolean isEvaluated = evaluationService.isUserEvaluated(userId, restaurantId);
        boolean isFavorite = restaurantFavoriteService.isUserFavorite(userId, restaurantId);
        boolean isIOS = RestaurantConstants.isIOS(userAgent);

        return RestaurantDetailDTO.from(restaurant, menus, isEvaluated, isFavorite, isIOS);
    }
}
