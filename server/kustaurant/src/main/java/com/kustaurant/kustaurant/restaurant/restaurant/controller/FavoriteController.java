package com.kustaurant.kustaurant.restaurant.restaurant.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.restaurant.controller.response.FavoriteResponse;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
@RequiredArgsConstructor
public class FavoriteController {

    private final RestaurantFavoriteService restaurantFavoriteService;

    // 식당 즐겨찾기 추가
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PutMapping("/web/api/restaurants/{restaurantId}/favorite")
    public ResponseEntity<FavoriteResponse> addRestaurantFavorite(
            @PathVariable Long restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        boolean result = restaurantFavoriteService.addFavorite(user.id(), restaurantId);
        long count = restaurantFavoriteService.countByRestaurantId(restaurantId);

        return ResponseEntity.ok(new FavoriteResponse(result, count));
    }

    // 식당 즐겨찾기 제거
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @DeleteMapping("/web/api/restaurants/{restaurantId}/favorite")
    public ResponseEntity<FavoriteResponse> deleteRestaurantFavorite(
            @PathVariable Long restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        boolean result = restaurantFavoriteService.deleteFavorite(user.id(), restaurantId);
        long count = restaurantFavoriteService.countByRestaurantId(restaurantId);

        return ResponseEntity.ok(new FavoriteResponse(result, count));
    }
}

