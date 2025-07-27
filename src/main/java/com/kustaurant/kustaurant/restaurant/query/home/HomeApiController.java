package com.kustaurant.kustaurant.restaurant.query.home;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeApiController {
    private final HomeBannerApiService homeBannerApiService;
    private final RestaurantHomeService restaurantHomeService;

    @Operation(summary = "홈화면 top맛집, 나를 위한 맛집, 배너 이미지 불러오기", description = "top 맛집과 나를 위한 맛집 리스트인 topRestaurantsByRating, restaurantsForMe 을 반환하고 홈의 배너 이미지 url리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "restaurant found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantListsResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurant not found", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/api/v1/home")
    public ResponseEntity<RestaurantListsResponse> home(@Parameter(hidden = true) @AuthUser AuthUserInfo user) {
        List<RestaurantCoreInfoDto> topRestaurantsByRatingDTOs = restaurantHomeService.getTopRestaurants(
                user.id()); // 점수 높은 순으로 총 16개
        // 로그인 여부에 따라 랜덤 식당 또는 추천 식당을 반환하는 서비스 메서드를 호출합니다.
        List<RestaurantCoreInfoDto> restaurantsForMeDTOs = restaurantHomeService.getRecommendedOrRandomRestaurants(user.id());
        // 홈화면의 배너 이미지
        List<String> homePhotoUrls = homeBannerApiService.getHomeBannerImage();
        RestaurantListsResponse response = new RestaurantListsResponse(
                topRestaurantsByRatingDTOs,
                restaurantsForMeDTOs,
                homePhotoUrls
        );
        return ResponseEntity.ok(response);
    }
}
