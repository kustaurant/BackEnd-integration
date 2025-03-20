package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.common.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantMenuDomain;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantDetailDTO;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.RestaurantSpecification;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    // repository
    private final RestaurantRepository restaurantRepository;
    // service
    private final RestaurantFavoriteService restaurantFavoriteService;
    private final RestaurantMenuService restaurantMenuService;
    private final EvaluationService evaluationService;

    public RestaurantDomain getDomain(Integer id) {
        return restaurantRepository.getById(id);
    }

    public RestaurantDetailDTO getRestaurantDetailDto(Integer restaurantId, User user, String userAgent) {
        RestaurantDomain restaurant = restaurantRepository.getById(restaurantId);

        return RestaurantDetailDTO.from(
                restaurant,
                restaurantMenuService.findMenusByRestaurantId(restaurantId),
                evaluationService.isUserEvaluated(user, restaurant),
                restaurantFavoriteService.isUserFavorite(user, restaurant),
                RestaurantConstants.isIOS(userAgent)
        );
    }
}
