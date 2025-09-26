package com.kustaurant.kustaurant.restaurant.query.home;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeApiController implements HomeApiDoc {
    private final HomeBannerApiService homeBannerApiService;
    private final RestaurantHomeService restaurantHomeService;

    @GetMapping("/api/v2/home")
    public ResponseEntity<RestaurantListsResponse> home(
            @AuthUser AuthUserInfo user
    ) {
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
