package com.kustaurant.kustaurant.restaurant.restaurant.service;

import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationQueryService;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantMenu;
import com.kustaurant.kustaurant.restaurant.restaurant.controller.response.RestaurantDetailDTO;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantDetailRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantFavoriteCheckRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantMenuRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantQueryService {

    private final RestaurantDetailRepository restaurantDetailRepository;

    public RestaurantDetail getRestaurantDetail(Integer restaurantId, Long userId) {
        return restaurantDetailRepository.getRestaurantDetail(restaurantId, userId);
    }

    // repository
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuRepository restaurantMenuRepository;
    private final RestaurantFavoriteCheckRepository favoriteCheckRepository;
    // service
    private final EvaluationQueryService evaluationQueryService;

    public Restaurant getActiveDomain(Integer restaurantId) {
        return restaurantRepository.getByIdAndStatus(restaurantId, "ACTIVE");
    }

    public RestaurantDetailDTO getActiveRestaurantDetailDto(Integer restaurantId, Long userId) {
        Restaurant restaurant = restaurantRepository.getByIdAndStatus(restaurantId, "ACTIVE");
        List<RestaurantMenu> menus = restaurantMenuRepository.findByRestaurantOrderById(restaurantId);

        boolean isEvaluated = evaluationQueryService.isUserEvaluated(userId, restaurantId);
        boolean isFavorite = favoriteCheckRepository.isUserFavorite(userId, restaurantId);

        return RestaurantDetailDTO.from(restaurant, menus, isEvaluated, isFavorite);
    }
}
