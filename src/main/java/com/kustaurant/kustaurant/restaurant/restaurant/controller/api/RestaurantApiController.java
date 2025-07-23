package com.kustaurant.kustaurant.restaurant.restaurant.controller.api;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantService;
import com.kustaurant.kustaurant.restaurant.restaurant.controller.response.RestaurantDetailDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @author Ding
 * @since 2024.7.10.
 * description: restaurant controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RestaurantApiController {

    private final RestaurantService restaurantService;

    @Operation(summary = "식당 상세 화면 정보 불러오기", description = "식당 하나에 대한 상세 정보가 반환됩니다. (mainTier가 -1인 것은 티어가 아직 매겨지지 않은 식당입니다.)\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - restaurantId: not null\n\n" +
            "   - restaurantImgUrl: not null\n\n" +
            "   - mainTier: not null\n\n" +
            "   - restaurantCuisine: not null\n\n" +
            "   - restaurantCuisineImgUrl: not null\n\n" +
            "   - restaurantPosition: not null\n\n" +
            "   - restaurantName: not null\n\n" +
            "   - restaurantAddress: not null\n\n" +
            "   - isOpen: not null\n\n" +
            "   - businessHours: not null\n\n" +
            "   - naverMapUrl: not null\n\n" +
            "   - situationList: **null이거나 빈 배열일 수 있습니다.**\n\n" +
            "   - partnershipInfo: **null일 수 있습니다.**\n\n" +
            "   - evaluationCount: not null\n\n" +
            "   - restaurantScore: **null일 수 있습니다.**(데이터가 없을 경우)\n\n" +
            "   - isEvaluated: not null\n\n" +
            "   - isFavorite: not null\n\n" +
            "   - favoriteCount: not null\n\n" +
            "   - restaurantMenuList: **null이거나 빈 배열**이 넘어갈 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식당 존재함. 응답 정상 반환.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantDetailDTO.class))}),
            @ApiResponse(responseCode = "404", description = "해당 id를 가진 식당이 없음. 또는 폐업함.", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDetailDTO> getRestaurantDetail(
            @PathVariable @Parameter(required = true, description = "식당 id", example = "1") Integer restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        return new ResponseEntity<>(
                restaurantService.getActiveRestaurantDetailDto(restaurantId, user.id()),
                HttpStatus.OK
        );
    }

    @Operation(summary = "식당 상세 화면 정보 불러오기 (Auth 버전)", description = "식당 하나에 대한 상세 정보가 반환됩니다. (mainTier가 -1인 것은 티어가 아직 매겨지지 않은 식당입니다.)\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - restaurantId: not null\n\n" +
            "   - restaurantImgUrl: not null\n\n" +
            "   - mainTier: not null\n\n" +
            "   - restaurantCuisine: not null\n\n" +
            "   - restaurantCuisineImgUrl: not null\n\n" +
            "   - restaurantPosition: not null\n\n" +
            "   - restaurantName: not null\n\n" +
            "   - restaurantAddress: not null\n\n" +
            "   - isOpen: not null\n\n" +
            "   - businessHours: not null\n\n" +
            "   - naverMapUrl: not null\n\n" +
            "   - situationList: **null이거나 빈 배열일 수 있습니다.**\n\n" +
            "   - partnershipInfo: **null일 수 있습니다.**\n\n" +
            "   - evaluationCount: not null\n\n" +
            "   - restaurantScore: **null일 수 있습니다.**(데이터가 없을 경우)\n\n" +
            "   - isEvaluated: not null\n\n" +
            "   - isFavorite: not null\n\n" +
            "   - favoriteCount: not null\n\n" +
            "   - restaurantMenuList: **null이거나 빈 배열**이 넘어갈 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식당 존재함. 응답 정상 반환.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantDetailDTO.class))}),
            @ApiResponse(responseCode = "404", description = "해당 id를 가진 식당이 없음. 또는 폐업함.", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/auth/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDetailDTO> getRestaurantDetailWithAuth(
            @PathVariable @Parameter(required = true, description = "식당 id", example = "1") Integer restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        return new ResponseEntity<>(
                restaurantService.getActiveRestaurantDetailDto(restaurantId, user.id()),
                HttpStatus.OK
        );
    }




}
