package com.kustaurant.restauranttier.tab1_home.controller;

import com.kustaurant.restauranttier.tab1_home.dto.RestaurantHomeDTO;
import com.kustaurant.restauranttier.tab1_home.dto.RestaurantListsResponse;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantTierDTO;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class HomeApiController {
    private final RestaurantApiService restaurantApiService;

    @Operation(summary = "홈화면 top맛집, 나를 위한 맛집, 슬라이드 이미지 불러오기", description = "top 맛집과 나를 위한 맛집 리스트인 topRestaurantsByRating, restaurantsForMe 을 반환하고 홈의 배너 이미지 url리스트를 반환합니다. 현재 배너 이미지 url은 임시 이미지입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "restaurant found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantListsResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurant not found", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/api/v1/home")
    public ResponseEntity<RestaurantListsResponse> home() {
        List<Restaurant> topRestaurantsByRating = restaurantApiService.getTopRestaurants(); // 점수 높은 순으로 총 16개
        List<Restaurant> restaurantsForMe = restaurantApiService.getTopRestaurants(); // 임시로 설정


        List<RestaurantTierDTO> topRestaurantsByRatingDTOs = topRestaurantsByRating.stream()
                .map(restaurant -> RestaurantTierDTO.convertRestaurantToTierDTO(restaurant,null,null,null))
                .collect(Collectors.toList());
        List<RestaurantTierDTO> restaurantsForMeDTOs = topRestaurantsByRating.stream()
                .map(restaurant -> RestaurantTierDTO.convertRestaurantToTierDTO(restaurant,null,null,null))
                .collect(Collectors.toList());

        // 홈화면의 배너 이미지 (현재는 하드코딩된 상태)
        List<String> homePhotoUrls = topRestaurantsByRating.stream().limit(5)
                .map(Restaurant::getRestaurantImgUrl)
                .collect(Collectors.toList());
        RestaurantListsResponse response = new RestaurantListsResponse(topRestaurantsByRatingDTOs, restaurantsForMeDTOs, homePhotoUrls);
        return ResponseEntity.ok(response);
    }

}
