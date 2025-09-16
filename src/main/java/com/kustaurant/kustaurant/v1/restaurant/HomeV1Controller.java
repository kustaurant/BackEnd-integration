package com.kustaurant.kustaurant.v1.restaurant;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.CuisineList;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.LocationList;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.draw.RestaurantDrawService;
import com.kustaurant.kustaurant.restaurant.query.home.HomeBannerApiService;
import com.kustaurant.kustaurant.restaurant.query.home.RestaurantHomeService;
import com.kustaurant.kustaurant.restaurant.query.search.RestaurantSearchService;
import com.kustaurant.kustaurant.v1.restaurant.response.RestaurantListsResponse;
import com.kustaurant.kustaurant.v1.restaurant.response.RestaurantTierDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeV1Controller {

    private final HomeBannerApiService homeBannerApiService;
    private final RestaurantHomeService restaurantHomeService;
    private final RestaurantSearchService restaurantSearchService;
    private final RestaurantDrawService drawService;

    @GetMapping("/api/v1/home")
    public ResponseEntity<RestaurantListsResponse> home(
            @AuthUser AuthUserInfo user
    ) {
        List<RestaurantCoreInfoDto> topRestaurantsByRatingDTOs = restaurantHomeService.getTopRestaurants(
                user.id()); // 점수 높은 순으로 총 16개
        // 로그인 여부에 따라 랜덤 식당 또는 추천 식당을 반환하는 서비스 메서드를 호출합니다.
        List<RestaurantCoreInfoDto> restaurantsForMeDTOs = restaurantHomeService.getRecommendedOrRandomRestaurants(user.id());
        // 홈화면의 배너 이미지
        List<String> homePhotoUrls = homeBannerApiService.getHomeBannerImage();
        com.kustaurant.kustaurant.restaurant.query.home.RestaurantListsResponse response =
                new com.kustaurant.kustaurant.restaurant.query.home.RestaurantListsResponse(
                        topRestaurantsByRatingDTOs,
                        restaurantsForMeDTOs,
                        homePhotoUrls
                );
        return ResponseEntity.ok(
                RestaurantListsResponse.fromV2(response));
    }

    @GetMapping("/api/v1/search")
    public ResponseEntity<List<RestaurantTierDTO>> search(
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @AuthUser AuthUserInfo user
    ) {
        if (kw == null || kw.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        String[] kwList = kw.split(" ");

        return ResponseEntity.ok(restaurantSearchService
                .search(kwList, user.id()).stream().map(RestaurantTierDTO::fromV2).toList());
    }

    @GetMapping("/api/v1/draw")
    public ResponseEntity<List<RestaurantTierDTO>> getRestaurantListForCuisine(
            @CuisineList List<String> cuisines,
            @LocationList List<String> locations
    ) {
        return new ResponseEntity<>(
                drawService.draw(cuisines, locations).stream().map(RestaurantTierDTO::fromV2).toList(),
                HttpStatus.OK);
    }
}
