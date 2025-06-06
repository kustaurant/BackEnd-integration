package com.kustaurant.kustaurant.web.discovery;

import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.dto.RestaurantTierDataClass;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.restaurant.presentation.web.RestaurantWebService;
import com.kustaurant.kustaurant.global.auth.session.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchWebController {

    private final EvaluationService evaluationService;
    private final CustomOAuth2UserService userService;
    private final RestaurantWebService restaurantWebService;

    // 검색 결과 화면
    @GetMapping("/search")
    public String search(
            Model model,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            Principal principal
    ) {
        UserEntity UserEntity = userService.getUserByPrincipal(principal);

        if (kw.isEmpty()) {
            model.addAttribute("kw", "입력된 검색어가 없습니다.");
            return "searchResult";
        } else {
            model.addAttribute("kw", kw);
        }

        String[] kwList = kw.split(" "); // 검색어 공백 단위로 끊음
        List<RestaurantEntity> restaurantList = restaurantWebService.searchRestaurants(kwList);

        List<RestaurantTierDataClass> restaurantTierDataClassList = evaluationService.convertToTierDataClassList(restaurantList, UserEntity, false);

        model.addAttribute("restaurantTierData", restaurantTierDataClassList);

        return "searchResult";
    }

}
