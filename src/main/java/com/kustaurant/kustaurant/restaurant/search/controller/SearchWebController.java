package com.kustaurant.kustaurant.restaurant.search.controller;

import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.tier.RestaurantTierDataClass;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.global.auth.session.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchWebController {

    private final EvaluationService evaluationService;

    // 검색 결과 화면
    @GetMapping("/search")
    public String search(
            Model model,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @AuthUser AuthUserInfo user
    ) {
        if (kw.isEmpty()) {
            model.addAttribute("kw", "입력된 검색어가 없습니다.");
            return "searchResult";
        } else {
            model.addAttribute("kw", kw);
        }

        String[] kwList = kw.split(" "); // 검색어 공백 단위로 끊음
//        List<RestaurantEntity> restaurantList = restaurantWebService.searchRestaurants(kwList);
        List<RestaurantEntity> restaurantList = List.of();

        List<RestaurantTierDataClass> restaurantTierDataClassList = evaluationService.convertToTierDataClassList(restaurantList, user.id(), false);

        model.addAttribute("restaurantTierData", restaurantTierDataClassList);

        return "searchResult";
    }

}
