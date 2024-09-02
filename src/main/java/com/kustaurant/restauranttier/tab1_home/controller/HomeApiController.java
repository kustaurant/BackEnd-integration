package com.kustaurant.restauranttier.tab1_home.controller;

import com.kustaurant.restauranttier.common.UserService;
import com.kustaurant.restauranttier.common.apiUser.customAnno.JwtToken;
import com.kustaurant.restauranttier.tab1_home.dto.RestaurantListsResponse;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantTierDTO;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantApiService;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantWebService;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class HomeApiController {
    private final RestaurantApiService restaurantApiService;
    private final RestaurantWebService restaurantWebService;
    private final UserService userService;

    @Operation(summary = "홈화면 top맛집, 나를 위한 맛집, 배너 이미지 불러오기", description = "top 맛집과 나를 위한 맛집 리스트인 topRestaurantsByRating, restaurantsForMe 을 반환하고 홈의 배너 이미지 url리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "restaurant found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantListsResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurant not found", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/api/v1/home")
    public ResponseEntity<RestaurantListsResponse> home(@JwtToken @Parameter(hidden = true)Integer userId) {
        List<Restaurant> topRestaurantsByRating = restaurantApiService.getTopRestaurants(); // 점수 높은 순으로 총 16개

        // 로그인 여부에 따라 랜덤 식당 또는 추천 식당을 반환하는 서비스 메서드를 호출합니다.
        List<Restaurant> restaurantsForMe = restaurantApiService.getRecommendedOrRandomRestaurants(userId);

        List<RestaurantTierDTO> topRestaurantsByRatingDTOs = topRestaurantsByRating.stream()
                .map(restaurant -> RestaurantTierDTO.convertRestaurantToTierDTO(restaurant, null, null, null))
                .collect(Collectors.toList());
        List<RestaurantTierDTO> restaurantsForMeDTOs = restaurantsForMe.stream()
                .map(restaurant -> RestaurantTierDTO.convertRestaurantToTierDTO(restaurant, null, null, null))
                .collect(Collectors.toList());

        // 홈화면의 배너 이미지
        List<String> homePhotoUrls = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            StringBuilder imagePathBuilder = new StringBuilder();
            imagePathBuilder.append("/배너").append(i).append(".png");

            StringBuilder imageUrlBuilder = new StringBuilder();
            imageUrlBuilder.append("https://kustaurant.s3.ap-northeast-2.amazonaws.com/home").append(imagePathBuilder);

            homePhotoUrls.add(imageUrlBuilder.toString());
        }




        RestaurantListsResponse response = new RestaurantListsResponse(topRestaurantsByRatingDTOs, restaurantsForMeDTOs, homePhotoUrls);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/search")
    @Operation(summary = "검색하기", description = "리스트 순서대로 출력해주시면 됩니다! 빈 배열인 경우 해당하는 식당이 없다는 화면을 보여주시면 됩니다!\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - restaurantId: not null\n\n" +
            "   - restaurantRanking: **null입니다.**\n\n" +
            "   - restaurantName: not null\n\n" +
            "   - restaurantCuisine: not null\n\n" +
            "   - restaurantPosition: not null\n\n" +
            "   - restaurantImgUrl: not null\n\n" +
            "   - mainTier: not null\n\n" +
            "   - isEvaluated: not null\n\n" +
            "   - isFavorite: not null\n\n" +
            "   - x: not null\n\n" +
            "   - y: not null\n\n" +
            "   - partnershipInfo: **null일 수 있습니다.**\n\n" +
            "   - restaurantScore: **null일 수 있습니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantTierDTO.class)))}),
    })
    public ResponseEntity<List<RestaurantTierDTO>> search(
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        if (kw == null || kw.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        User user = userService.findUserById(userId);

        String[] kwList = kw.split(" ");
        List<Restaurant> restaurantList = restaurantWebService.searchRestaurants(kwList);

        return ResponseEntity.ok(restaurantList.stream().map(restaurant ->
                RestaurantTierDTO.convertRestaurantToTierDTO(restaurant, null, restaurantApiService.isEvaluated(restaurant, user), restaurantApiService.isFavorite(restaurant, user)))
                .toList());
    }
}
