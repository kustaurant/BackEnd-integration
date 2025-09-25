package com.kustaurant.kustaurant.restaurant.restaurant.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.restaurant.controller.response.FavoriteResponse;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FavoriteApiController implements FavoriteApiDoc {

    private final RestaurantFavoriteService restaurantFavoriteService;

    @PutMapping("/v2/auth/restaurants/{restaurantId}/favorite")
    public ResponseEntity<FavoriteResponse> addRestaurantFavorite(
            @PathVariable Long restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        // 즐겨찾기 로직
        boolean result = restaurantFavoriteService.addFavorite(user.id(), restaurantId);
        // 즐겨찾기 이후 결과(즐겨찾기가 해제됐는지, 추가됐는지와 해당 식당의 즐겨찾기 개수)를 반환
        long count = restaurantFavoriteService.countByRestaurantId(restaurantId);
        return ResponseEntity.ok(new FavoriteResponse(result, count));
    }

    @DeleteMapping("/v2/auth/restaurants/{restaurantId}/favorite")
    public ResponseEntity<FavoriteResponse> deleteRestaurantFavorite(
            @PathVariable Long restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        // 즐겨찾기 로직
        boolean result = restaurantFavoriteService.deleteFavorite(user.id(), restaurantId);
        // 즐겨찾기 이후 결과(즐겨찾기가 해제됐는지, 추가됐는지와 해당 식당의 즐겨찾기 개수)를 반환
        long count = restaurantFavoriteService.countByRestaurantId(restaurantId);
        return ResponseEntity.ok(new FavoriteResponse(result, count));
    }
}
