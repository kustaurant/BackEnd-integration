package com.kustaurant.kustaurant.common.restaurant.application.service.command;

import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.common.restaurant.application.constants.RestaurantConstants;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantMenu;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.dto.RestaurantDetailDTO;
import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.port.RestaurantRepository;
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

    public RestaurantDetailDTO getActiveRestaurantDetailDto(Integer restaurantId, Integer userId, String userAgent) {
        Restaurant restaurant = restaurantRepository.getByIdAndStatus(restaurantId, "ACTIVE");
        List<RestaurantMenu> menus = restaurantMenuService.findMenusByRestaurantId(restaurantId);

        boolean isEvaluated = evaluationService.isUserEvaluated(userId, restaurantId);
        boolean isFavorite = restaurantFavoriteService.isUserFavorite(userId, restaurantId);
        boolean isIOS = RestaurantConstants.isIOS(userAgent);

        return RestaurantDetailDTO.from(restaurant, menus, isEvaluated, isFavorite, isIOS);
    }
}
