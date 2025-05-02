package com.kustaurant.kustaurant.web.main;

import com.kustaurant.kustaurant.common.modal.service.HomeModalService;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.common.modal.infrastructure.HomeModalEntity;
import com.kustaurant.kustaurant.common.restaurant.presentation.web.RestaurantWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final RestaurantWebService restaurantWebService;
    private final HomeModalService homeModalService;

    //@Value("#{'${restaurant.cuisines}'.split(',\\s*')}")
    private List<String> cuisines = Arrays.asList(
            "전체", "한식", "일식", "중식", "양식", "아시안", "고기",
            "치킨", "해산물", "햄버거/피자", "분식", "술집", "카페/디저트",
            "베이커리", "기타"
    );

    @RequestMapping("/temp")
    public String temp() {
        return "temp";
    }

    @GetMapping("/home")
    public String home() {
        return "redirect:/";
    }

    @GetMapping("/swagger-update")
    public String swaggerUpdate() {
        return "swagger-update";
    }


    // 홈 화면
    @GetMapping("/")
    public String root(
            Model model
    ) {
        List<RestaurantEntity> restaurants = restaurantWebService.getTopRestaurants();
        List<String> cuisines = new ArrayList<>(Arrays.asList("한식","일식","중식","양식","아시안","고기","치킨","햄버거","분식","해산물","술집","샐러드","카페","베이커리","기타","전체"));

        HomeModalEntity homeModal = homeModalService.get();

        model.addAttribute("cuisines", cuisines);
        model.addAttribute("restaurants",restaurants);
        model.addAttribute("currentPage","home");
        model.addAttribute("homeModal", homeModal);
        return "home";
    }

    // 이용약관
    @GetMapping("/terms_of_use")
    public String terms_of_use(){
        return "terms_of_use";
    }

    // 개인정보 처리방침
    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }

    // 마케팅
    @GetMapping("/marketing")
    public String marketing() {
        return "marketing";
    }

}




