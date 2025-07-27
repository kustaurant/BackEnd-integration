package com.kustaurant.kustaurant.restaurant.restaurant.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;

import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantQueryService;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class RestaurantWebController {

    private final RestaurantQueryService restaurantQueryService;

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
        RestaurantDetail detailDto = restaurantQueryService.getRestaurantDetail(restaurantId,
                user.id());

        model.addAttribute("restaurantDto", detailDto);
        boolean hasEvaluated = detailDto.getIsEvaluated();
        String evaluationButton = hasEvaluated ? "다시 평가하기" : " 평가하기";
        model.addAttribute("evaluationButton", evaluationButton);

        // 메뉴 정보
        model.addAttribute("menus", detailDto.getRestaurantMenuList());

        return "restaurant";
    }


}