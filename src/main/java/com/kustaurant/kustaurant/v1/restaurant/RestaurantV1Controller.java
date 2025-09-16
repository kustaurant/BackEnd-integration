package com.kustaurant.kustaurant.v1.restaurant;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantQueryService;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;
import com.kustaurant.kustaurant.v1.restaurant.response.RestaurantDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RestaurantV1Controller {

    private final RestaurantQueryService restaurantQueryService;

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDetailDTO> getRestaurantDetail(
            @PathVariable Integer restaurantId,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @AuthUser AuthUserInfo user
    ) {
        // 식당 데이터 DTO로 변환하기
        RestaurantDetail detail = restaurantQueryService.getRestaurantDetail((long) restaurantId, user.id());

        return new ResponseEntity<>(RestaurantDetailDTO.fromV2(detail), HttpStatus.OK);
    }

    @GetMapping("/auth/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDetailDTO> getRestaurantDetailWithAuth(
            @PathVariable Integer restaurantId,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @AuthUser AuthUserInfo user
    ) {
        // 식당 데이터 DTO로 변환하기
        RestaurantDetail detail = restaurantQueryService.getRestaurantDetail((long) restaurantId, user.id());

        return new ResponseEntity<>(RestaurantDetailDTO.fromV2(detail), HttpStatus.OK);
    }
}
