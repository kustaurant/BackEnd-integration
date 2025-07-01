package com.kustaurant.kustaurant.restaurant.restaurant.controller.web;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantFavoriteService;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantService;

import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class RestaurantWebController {

    private final RestaurantService restaurantService;
    private final RestaurantWebService restaurantWebService;
    private final RestaurantFavoriteService restaurantFavoriteService;

    @Value("${restaurant.initialDisplayMenuCount}")
    private int initialDisplayMenuCount;

    @GetMapping("/restaurants/{restaurantId}")
    public String restaurant(
            Model model,
            @PathVariable Integer restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        model.addAttribute("initialDisplayMenuCount", initialDisplayMenuCount);
        // 식당 정보
        RestaurantDetailWebDto restaurantDetailWebDto = restaurantWebService.getRestaurantWebDetails(user.id(), restaurantId);
        model.addAttribute("restaurantDto", restaurantDetailWebDto);
        String evaluationButton =
                (user != null && restaurantDetailWebDto.getRestaurantDetail().getIsEvaluated()) ? "다시 평가하기" : " 평가하기";
        model.addAttribute("evaluationButton", evaluationButton);
        // 메뉴 정보
        model.addAttribute("menus", restaurantDetailWebDto.getRestaurantDetail().getRestaurantMenuList());
        // 식당 댓글
        model.addAttribute("restaurantComments", restaurantDetailWebDto.getComments());

        return "restaurant";
    }

    // 식당 즐겨찾기
    @PostMapping("/web/api/restaurants/{restaurantId}/favorite/toggle")
    public ResponseEntity<Boolean> toggleFavorite(
            @PathVariable Integer restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        Restaurant restaurant = restaurantService.getActiveDomain(restaurantId);

        return ResponseEntity.ok(restaurantFavoriteService.toggleFavorite(user.id(), restaurantId));
    }

}