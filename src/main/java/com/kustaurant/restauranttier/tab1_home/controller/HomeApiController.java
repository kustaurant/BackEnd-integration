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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class HomeApiController {
    private final RestaurantApiService restaurantApiService;

    @Operation(summary = "홈화면 top맛집, 나를 위한 맛집, 배너 이미지 불러오기", description = "top 맛집과 나를 위한 맛집 리스트인 topRestaurantsByRating, restaurantsForMe 을 반환하고 홈의 배너 이미지 url리스트를 반환합니다. 현재 배너 이미지 url은 임시 이미지입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "restaurant found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantListsResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurant not found", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/api/v1/home")
    public ResponseEntity<RestaurantListsResponse> home() {
        List<Restaurant> topRestaurantsByRating = restaurantApiService.getTopRestaurants(); // 점수 높은 순으로 총 16개
        List<Restaurant> restaurantsForMe = restaurantApiService.getRestaurantsByCuisinesAndLocations("ALL","ALL",null,false); // 임시로 설정
        Random rand = new Random();

        // 결과 리스트를 랜덤으로 섞음
        Collections.shuffle(restaurantsForMe, rand);
        restaurantsForMe.subList(0,15);


        List<RestaurantTierDTO> topRestaurantsByRatingDTOs = topRestaurantsByRating.stream()
                .map(restaurant -> RestaurantTierDTO.convertRestaurantToTierDTO(restaurant,null,null,null))
                .collect(Collectors.toList());
        List<RestaurantTierDTO> restaurantsForMeDTOs = restaurantsForMe.stream()
                .map(restaurant -> RestaurantTierDTO.convertRestaurantToTierDTO(restaurant,null,null,null))
                .collect(Collectors.toList());

        // TODO:토큰을 통해 로그인 되어있는지 확인하고 로그인되어있으면 즐겨찾기 기반 추천, 안되어있을 시 랜덤 추천
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//            // 유저 ID를 토큰에서 추출
//            Long userId = jwtTokenProvider.getUserIdFromToken(token);
//            // 로그인된 유저의 즐겨찾기 기반 추천
//            restaurantsForMe = restaurantApiService.getRecommendedRestaurantsForUser(userId);
//        } else {
//            // 비로그인 유저를 위한 랜덤 추천
//            restaurantsForMe = restaurantApiService.getRandomRestaurants();
//        }

        // 홈화면의 배너 이미지 TODO: 현재 임시이미지고 실제로 구현해야함
        List<String> homePhotoUrls = new ArrayList<>();

        String imagePath = "/img/home/배너1.png";
        // 서버의 도메인 주소와 이미지 경로를 합쳐서 전체 URL을 생성합니다.
        String imageUrl = "http://3.35.154.191:8080" + imagePath;
        homePhotoUrls.add(imageUrl);
        // 임시 배너 이미지 추가
        List<String> tempUrls = topRestaurantsByRating.stream().limit(4)
                .map(Restaurant::getRestaurantImgUrl)
                .toList();
        homePhotoUrls.addAll(tempUrls);
        RestaurantListsResponse response = new RestaurantListsResponse(topRestaurantsByRatingDTOs, restaurantsForMeDTOs, homePhotoUrls);
        return ResponseEntity.ok(response);
    }

}
