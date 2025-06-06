package com.kustaurant.kustaurant.common.restaurant.presentation.api;

import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.RestaurantFavoriteService;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.RestaurantService;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.OUserService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.JwtToken;
import com.kustaurant.kustaurant.global.exception.ErrorResponse;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.dto.FavoriteResponseDTO;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.dto.RestaurantDetailDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
    private final OUserService userService;
    private final RestaurantFavoriteService restaurantFavoriteService;

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
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        return new ResponseEntity<>(
                restaurantService.getActiveRestaurantDetailDto(restaurantId, userId, userAgent),
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
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        return new ResponseEntity<>(
                restaurantService.getActiveRestaurantDetailDto(restaurantId, userId, userAgent),
                HttpStatus.OK
        );
    }


    // 즐겨찾기
    @PostMapping("/auth/restaurants/{restaurantId}/favorite-toggle")
    @Operation(summary = "즐겨찾기 추가/해제 토글", description = "즐겨찾기 버튼을 누른 후의 즐겨찾기 상태를 반환합니다.\n\n눌러서 즐겨찾기가 해제된 경우 -> false반환\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - boolean: not null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "404", description = "retaurantId에 해당하는 식당이 존재하지 않을 때 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<Boolean> restaurantFavoriteToggle(
            @PathVariable Integer restaurantId,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        // 유저 가져오기
        UserEntity UserEntity = userService.findUserById(userId);
        // 식당 가져오기
        Restaurant restaurant = restaurantService.getActiveDomain(restaurantId);
        // 즐겨찾기 로직
        boolean result = restaurantFavoriteService.toggleFavorite(UserEntity, restaurant);
        // 즐겨찾기 이후 결과(즐겨찾기가 해제됐는지, 추가됐는지와 해당 식당의 즐겨찾기 개수)를 반환
        return ResponseEntity.ok(result);
    }

    // 즐겨찾기2
    @PostMapping("/auth/restaurants/{restaurantId}/favorite-toggle2")
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
            @ApiResponse(responseCode = "200", description = "success", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FavoriteResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "retaurantId에 해당하는 식당이 존재하지 않을 때 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<FavoriteResponseDTO> restaurantFavoriteToggle2(
            @PathVariable Integer restaurantId,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        // 유저 가져오기
        UserEntity UserEntity = userService.findUserById(userId);
        // 식당 가져오기
        Restaurant restaurant = restaurantService.getActiveDomain(restaurantId);
        // 즐겨찾기 로직
        boolean result = restaurantFavoriteService.toggleFavorite(UserEntity, restaurant);
        // 즐겨찾기 이후 결과(즐겨찾기가 해제됐는지, 추가됐는지와 해당 식당의 즐겨찾기 개수)를 반환
        return ResponseEntity.ok(new FavoriteResponseDTO(result, restaurant.getFavoriteCount()));
    }

}
