package com.kustaurant.kustaurant.restaurant.favorite.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.favorite.controller.response.FavoriteResponse;
import com.kustaurant.kustaurant.restaurant.favorite.service.RestaurantFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class FavoriteController {

    private final RestaurantFavoriteService restaurantFavoriteService;

    // 식당 즐겨찾기
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PostMapping("/web/api/restaurants/{restaurantId}/favorite/toggle")
    public ResponseEntity<FavoriteResponse> toggleFavorite(
            @PathVariable Long restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        boolean result = restaurantFavoriteService.toggleFavorite(user.id(), restaurantId);
        long count = restaurantFavoriteService.countByRestaurantId(restaurantId);

        return ResponseEntity.ok(new FavoriteResponse(result, count));
    }
}
