package com.kustaurant.kustaurant.v1.restaurant;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.RestaurantFavoriteRepositoryImpl;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantFavoriteService;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantQueryService;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.v1.restaurant.response.FavoriteResponseDTO;
import com.kustaurant.kustaurant.v1.restaurant.response.RestaurantDetailDTO;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RestaurantV1Controller {

    private final RestaurantQueryService restaurantQueryService;
    private final RestaurantFavoriteService restaurantFavoriteService;
    private final RestaurantFavoriteRepository favoriteRepository;

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDetailDTO> getRestaurantDetail(
            @PathVariable Integer restaurantId,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 식당 데이터 DTO로 변환하기
        RestaurantDetail detail = restaurantQueryService.getRestaurantDetail((long) restaurantId, user.id());

        return new ResponseEntity<>(RestaurantDetailDTO.fromV2(detail), HttpStatus.OK);
    }

    @GetMapping("/auth/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDetailDTO> getRestaurantDetailWithAuth(
            @PathVariable Integer restaurantId,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 식당 데이터 DTO로 변환하기
        RestaurantDetail detail = restaurantQueryService.getRestaurantDetail((long) restaurantId, user.id());

        return new ResponseEntity<>(RestaurantDetailDTO.fromV2(detail), HttpStatus.OK);
    }

    // 즐겨찾기
    @PostMapping("/auth/restaurants/{restaurantId}/favorite-toggle")
    public ResponseEntity<Boolean> restaurantFavoriteToggle(
            @PathVariable Integer restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        boolean result;
        if (favoriteRepository.existsByUserIdAndRestaurantId(user.id(), (long) restaurantId)) {
            result = restaurantFavoriteService.deleteFavorite(user.id(), (long) restaurantId);
        } else {
            result = restaurantFavoriteService.addFavorite(user.id(), (long) restaurantId);
        }
        return ResponseEntity.ok(result);
    }

    // 즐겨찾기2
    @PostMapping("/auth/restaurants/{restaurantId}/favorite-toggle2")
    public ResponseEntity<FavoriteResponseDTO> restaurantFavoriteToggle2(
            @PathVariable Integer restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        boolean result;
        if (favoriteRepository.existsByUserIdAndRestaurantId(user.id(), (long) restaurantId)) {
            result = restaurantFavoriteService.deleteFavorite(user.id(), (long) restaurantId);
        } else {
            result = restaurantFavoriteService.addFavorite(user.id(), (long) restaurantId);
        }
        long count = restaurantFavoriteService.countByRestaurantId((long) restaurantId);
        return ResponseEntity.ok(new FavoriteResponseDTO(result, (int) count));
    }
}
