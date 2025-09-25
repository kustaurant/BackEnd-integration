package com.kustaurant.kustaurant.restaurant.restaurant.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.restaurant.restaurant.controller.response.FavoriteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface FavoriteApiDoc {

    @Operation(summary = "즐겨찾기 추가", description = "즐겨찾기 버튼을 누른 후의 즐겨찾기 상태와 수를 반환합니다.\n\n" +
            "[즐겨찾기 상태]\n\n" +
            "눌러서 즐겨찾기가 해제된 상태 -> false반환\n\n" +
            "눌러서 즐겨찾기가 추가된 상태 -> true반환\n\n" +
            "[즐겨찾기 수]\n\n" +
            "누른 후의 해당 식당 현재 즐겨찾기 수 반환\n\n" +
            "[반환 값 보충 설명]\n\n" +
            "   - boolean: not null\n\n" +
            "   - long: not null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = {@Content(schema = @Schema(implementation = FavoriteResponse.class))}),
            @ApiResponse(responseCode = "404", description = "retaurantId에 해당하는 식당이 존재하지 않을 때 404를 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    ResponseEntity<FavoriteResponse> addRestaurantFavorite(
            @PathVariable Long restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    );

    @Operation(summary = "즐겨찾기 제거", description = "즐겨찾기 버튼을 누른 후의 즐겨찾기 상태와 수를 반환합니다.\n\n" +
            "[즐겨찾기 상태]\n\n" +
            "눌러서 즐겨찾기가 해제된 상태 -> false반환\n\n" +
            "눌러서 즐겨찾기가 추가된 상태 -> true반환\n\n" +
            "[즐겨찾기 수]\n\n" +
            "누른 후의 해당 식당 현재 즐겨찾기 수 반환\n\n" +
            "[반환 값 보충 설명]\n\n" +
            "   - boolean: not null\n\n" +
            "   - long: not null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = {@Content(schema = @Schema(implementation = FavoriteResponse.class))}),
            @ApiResponse(responseCode = "404", description = "retaurantId에 해당하는 식당이 존재하지 않을 때 404를 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    ResponseEntity<FavoriteResponse> deleteRestaurantFavorite(
            @PathVariable Long restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    );
}
