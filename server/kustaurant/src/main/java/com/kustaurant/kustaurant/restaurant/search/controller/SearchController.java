package com.kustaurant.kustaurant.restaurant.search.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.search.service.RestaurantSearchV2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final RestaurantSearchV2Service restaurantSearchV2Service;

    // 검색 결과 화면
    @GetMapping("/search")
    public String search(
            Model model,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @AuthUser AuthUserInfo user
    ) {
        if (kw.isEmpty()) {
            model.addAttribute("kw", "입력된 검색어가 없습니다.");
            return "restaurant/searchResult";
        } else {
            model.addAttribute("kw", kw);
        }

        String[] kwList = kw.split(" "); // 검색어 공백 단위로 끊음
        List<RestaurantCoreInfoDto> result = restaurantSearchV2Service.search(kwList, user.id());

        model.addAttribute("searchResult", result);

        return "restaurant/searchResult";
    }

}
