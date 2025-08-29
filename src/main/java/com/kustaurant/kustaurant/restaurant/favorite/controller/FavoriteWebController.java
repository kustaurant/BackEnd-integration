package com.kustaurant.kustaurant.restaurant.favorite.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.favorite.controller.response.FavoriteResponseDTO;
import com.kustaurant.kustaurant.restaurant.favorite.service.RestaurantFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class FavoriteWebController {

    private final RestaurantFavoriteService restaurantFavoriteService;

    // 식당 즐겨찾기
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PostMapping("/web/api/restaurants/{restaurantId}/favorite/toggle")
    public ResponseEntity<FavoriteResponseDTO> toggleFavorite(
            @PathVariable Long restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        // 즐겨찾기 로직
        boolean result = restaurantFavoriteService.toggleFavorite(user.id(), restaurantId);
        // 즐겨찾기 이후 결과(즐겨찾기가 해제됐는지, 추가됐는지와 해당 식당의 즐겨찾기 개수)를 반환
        long count = restaurantFavoriteService.countByRestaurantId(restaurantId);
        return ResponseEntity.ok(new FavoriteResponseDTO(result, count));
    }
}
