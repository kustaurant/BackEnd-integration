package com.kustaurant.kustaurant.web.restaurant;

import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantTierDataClass;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.global.webUser.CustomOAuth2UserService;
import com.kustaurant.kustaurant.common.modal.HomeModal;
import com.kustaurant.kustaurant.common.modal.HomeModalRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.common.notice.FeedbackService;
import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final RestaurantRepository restaurantRepository;
    private final FeedbackService feedbackService;
    private final RestaurantWebService restaurantWebService;
    private final EvaluationService evaluationService;
    private final HomeModalRepository HomeModalRepository;
    private final CustomOAuth2UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

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
            Model model,
            Principal principal
    ) {
        List<RestaurantEntity> restaurants = restaurantWebService.getTopRestaurants();
        List<String> cuisines = new ArrayList<>(Arrays.asList("한식","일식","중식","양식","아시안","고기","치킨","햄버거","분식","해산물","술집","샐러드","카페","베이커리","기타","전체"));

        HomeModal homeModal = HomeModalRepository.getHomeModalByModalId(1);

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


    // 검색 결과 화면
    @GetMapping("/search")
    public String search(
            Model model,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            Principal principal
    ) {
        User user = userService.getUserByPrincipal(principal);

        if (kw.isEmpty()) {
            model.addAttribute("kw", "입력된 검색어가 없습니다.");
            return "searchResult";
        } else {
            model.addAttribute("kw", kw);
        }

        String[] kwList = kw.split(" "); // 검색어 공백 단위로 끊음
        List<RestaurantEntity> restaurantList = restaurantWebService.searchRestaurants(kwList);

        List<RestaurantTierDataClass> restaurantTierDataClassList = evaluationService.convertToTierDataClassList(restaurantList, user, false);

        model.addAttribute("restaurantTierData", restaurantTierDataClassList);

        return "searchResult";
    }

    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/feedback")
    public ResponseEntity<String> submitFeedback(
            @RequestBody Map<String, Object> jsonBody,
            Principal principal
    ) {
        String feedbackBody = jsonBody.get("feedbackBody").toString();

        if (feedbackBody.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("내용이 없습니다.");
        }

        String result = feedbackService.addFeedback(feedbackBody, principal);

        if (result.equals("fail")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("잘못된 접근입니다.");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("피드백 감사합니다.");
        }
    }
}




