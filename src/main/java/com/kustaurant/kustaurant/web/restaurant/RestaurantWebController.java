package com.kustaurant.kustaurant.web.restaurant;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantFavoriteService;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantService;
import com.kustaurant.kustaurant.common.user.infrastructure.User;

import com.kustaurant.kustaurant.global.webUser.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class RestaurantWebController {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final RestaurantService restaurantService;
    private final RestaurantWebService restaurantWebService;
    private final RestaurantFavoriteService restaurantFavoriteService;

    @Value("${restaurant.initialDisplayMenuCount}")
    private int initialDisplayMenuCount;

    @GetMapping("/restaurants/{restaurantId}")
    public String restaurant(
            Model model,
            @PathVariable Integer restaurantId,
            Principal principal
    ) {
        model.addAttribute("initialDisplayMenuCount", initialDisplayMenuCount);
        // 유저
        User user = principal == null ? null : customOAuth2UserService.getUser(principal.getName());
        // 식당 정보
        RestaurantDetailWebDto restaurantDetailWebDto = restaurantWebService.getRestaurantWebDetails(user, restaurantId);
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
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/restaurants/{restaurantId}/favorite/toggle")
    public ResponseEntity<Boolean> toggleFavorite(
            @PathVariable Integer restaurantId,
            Principal principal
    ) {
        User user = customOAuth2UserService.getUser(principal.getName());
        RestaurantDomain restaurant = restaurantService.getDomain(restaurantId);
        return ResponseEntity.ok(restaurantFavoriteService.toggleFavorite(user, restaurant));
    }

}