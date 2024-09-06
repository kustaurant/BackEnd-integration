package com.kustaurant.restauranttier.tab3_tier.controller;

import com.google.gson.Gson;
import com.kustaurant.restauranttier.tab1_home.controller.MainController;
import com.kustaurant.restauranttier.common.etc.JsonData;
import com.kustaurant.restauranttier.tab3_tier.entity.Evaluation;
import com.kustaurant.restauranttier.tab3_tier.entity.EvaluationItemScore;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.repository.EvaluationRepository;
import com.kustaurant.restauranttier.common.user.CustomOAuth2UserService;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantWebService;
import com.kustaurant.restauranttier.tab3_tier.service.EvaluationService;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class EvaluationController {
    private final RestaurantWebService restaurantWebService;
    private final EvaluationService evaluationService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final EvaluationRepository evaluationRepository;
    Gson gson = new Gson();

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // 평가 페이지 화면
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/evaluation/{restaurantId}")
    public String evaluation(
            Model model,
            Principal principal,
            @PathVariable Integer restaurantId
    ) {
        // TODO: 현재 평가를 막아놨습니다.
        Restaurant restaurant = restaurantWebService.getRestaurant(restaurantId);
        User user = customOAuth2UserService.getUser(principal.getName());
        Optional<Evaluation> evaluation = evaluationRepository.findByUserAndRestaurant(user, restaurant);
        Double mainScore = 0.0;

        if (evaluation.isPresent()) {

            mainScore = evaluation.get().getEvaluationScore();
        }
        model.addAttribute("mainScore", mainScore);
        model.addAttribute("restaurant", restaurant);

        return "evaluation";
    }

    // 평가 데이터 db 저장 (기존 평가 존재 시 업데이트 진행)
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/evaluation")
    public ResponseEntity<?> evaluationDBcreate(@RequestBody JsonData jsonData, Principal principal) {
        evaluationService.createOrUpdate(jsonData, principal);

        return ResponseEntity.ok("평가가 성공적으로 저장되었습니다.");
    }

}
