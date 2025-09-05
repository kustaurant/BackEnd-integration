package com.kustaurant.kustaurant.restaurant.favorite.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.restaurant.favorite.controller.response.FavoriteResponse;
import com.kustaurant.kustaurant.restaurant.favorite.service.RestaurantFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FavoriteApiController {

    private final RestaurantFavoriteService restaurantFavoriteService;

    // 즐겨찾기
    @PostMapping("/v2/auth/restaurants/{restaurantId}/favorite-toggle")
    @Operation(summary = "즐겨찾기 추가/해제 토글", description = "즐겨찾기 버튼을 누른 후의 즐겨찾기 상태를 반환합니다.\n\n눌러서 즐겨찾기가 해제된 경우 -> false반환\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - boolean: not null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = {@Content(schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "404", description = "retaurantId에 해당하는 식당이 존재하지 않을 때 404를 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    public ResponseEntity<Boolean> restaurantFavoriteToggle(
            @PathVariable Long restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 즐겨찾기 로직
        boolean result = restaurantFavoriteService.toggleFavorite(user.id(), restaurantId);
        // 즐겨찾기 이후 결과(즐겨찾기가 해제됐는지)를 반환
        return ResponseEntity.ok(result);
    }

    // 즐겨찾기2
    @Operation(summary = "즐겨찾기 추가/해제 토글", description = "즐겨찾기 버튼을 누른 후의 즐겨찾기 상태와 수를 반환합니다.\n\n" +
            "[즐겨찾기 상태]\n\n" +
            "눌러서 즐겨찾기가 해제된 경우 -> false반환\n\n" +
            "눌러서 즐겨찾기가 추가된 경우 -> true반환\n\n" +
            "[즐겨찾기 수]\n\n" +
            "누른 후의 해당 식당 현재 즐겨찾기 수 반환\n\n" +
            "[반환 값 보충 설명]\n\n" +
            "   - boolean: not null\n\n" +
            "   - int: not null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = {@Content(schema = @Schema(implementation = FavoriteResponse.class))}),
            @ApiResponse(responseCode = "404", description = "retaurantId에 해당하는 식당이 존재하지 않을 때 404를 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @PostMapping("/v2/auth/restaurants/{restaurantId}/favorite-toggle2")
    public ResponseEntity<FavoriteResponse> restaurantFavoriteToggle2(
            @PathVariable Long restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 즐겨찾기 로직
        boolean result = restaurantFavoriteService.toggleFavorite(user.id(), restaurantId);
        // 즐겨찾기 이후 결과(즐겨찾기가 해제됐는지, 추가됐는지와 해당 식당의 즐겨찾기 개수)를 반환
        long count = restaurantFavoriteService.countByRestaurantId(restaurantId);
        return ResponseEntity.ok(new FavoriteResponse(result, count));
    }
}
