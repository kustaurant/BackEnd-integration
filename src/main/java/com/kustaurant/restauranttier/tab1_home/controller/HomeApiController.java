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

    @Operation(summary = "홈화면 top맛집, 나를 위한 맛집 불러오기", description = "top 맛집과 나를 위한 맛집 리스트인 topRestaurantsByRating, restaurantsForMe 을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "restaurant found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantListsResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurant not found", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/api/v1/home")
    public ResponseEntity<RestaurantListsResponse> home() {
        List<Restaurant> topRestaurantsByRating = restaurantApiService.getTopRestaurants(); // 점수 높은 순으로 총 16개
        List<Restaurant> restaurantsForMe = restaurantApiService.getTopRestaurants(); // 임시로 설정
        List<RestaurantTierDTO> topRestaurantsByRatingDTOs = restaurantsForMe.stream()
                .map(restaurant -> RestaurantTierDTO.convertRestaurantToTierDTO(restaurant,null,null,null))
                .collect(Collectors.toList());
        List<RestaurantTierDTO> restaurantsForMeDTOs = restaurantsForMe.stream()
                .map(restaurant -> RestaurantTierDTO.convertRestaurantToTierDTO(restaurant,null,null,null))
                .collect(Collectors.toList());

        RestaurantListsResponse response = new RestaurantListsResponse(topRestaurantsByRatingDTOs, restaurantsForMeDTOs);
        return ResponseEntity.ok(response);
    }

}
