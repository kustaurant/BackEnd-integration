package com.kustaurant.kustaurant.restaurant.query.home;

import com.kustaurant.kustaurant.admin.modal.HomeModalEntity;
import com.kustaurant.kustaurant.admin.modal.HomeModalService;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final HomeModalService homeModalService;
    private final RestaurantHomeService restaurantHomeService;

    //@Value("#{'${restaurant.cuisines}'.split(',\\s*')}")
    private List<String> cuisines = Arrays.asList(
            "전체", "한식", "일식", "중식", "양식", "아시안", "고기",
            "치킨", "해산물", "햄버거/피자", "분식", "술집", "카페/디저트",
            "베이커리", "기타"
    );

    @RequestMapping("/temp")
    public String temp() {
        return "home/temp";
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
        List<RestaurantCoreInfoDto> topRestaurants = restaurantHomeService.getTopRestaurants(null);
        List<String> cuisines = new ArrayList<>(Arrays.asList("한식","일식","중식","양식","아시안","고기","치킨","햄버거","분식","해산물","술집","샐러드","카페","베이커리","기타","전체"));

        HomeModalEntity homeModal = homeModalService.get();

        model.addAttribute("cuisines", cuisines);
        model.addAttribute("restaurants", topRestaurants);
        model.addAttribute("currentPage","home");
        model.addAttribute("homeModal", homeModal);
        return "home/home";
    }

    // 이용약관
    @GetMapping("/terms_of_use")
    public String terms_of_use(){
        return "home/terms_of_use";
    }

    // 개인정보 처리방침
    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "home/privacy-policy";
    }

    // 마케팅
    @GetMapping("/marketing")
    public String marketing() {
        return "home/marketing";
    }

    @GetMapping("/web/api/auth/status")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    public ResponseEntity<Void> checkLoginStatus() {
        return ResponseEntity.ok().build();
    }

}




