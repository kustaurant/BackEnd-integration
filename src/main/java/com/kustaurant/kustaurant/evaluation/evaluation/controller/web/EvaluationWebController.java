package com.kustaurant.kustaurant.evaluation.evaluation.controller.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationCommandService;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationQueryService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantQueryService;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EvaluationWebController {
    private final RestaurantQueryService restaurantQueryService;

    private final EvaluationQueryService evaluationQueryService;
    private final EvaluationCommandService evaluationCommandService;

    // 1. 평가 페이지 화면
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @GetMapping("/evaluation/{restaurantId}")
    public String evaluation(
            Model model,
            @AuthUser AuthUserInfo user,
            @PathVariable Integer restaurantId
    ) {
        // 식당 정보
        Restaurant restaurant = restaurantQueryService.getActiveDomain(restaurantId);
        model.addAttribute("restaurant", restaurant);
        // 이전 평가 정보
        EvaluationDTO preEval = evaluationQueryService.getPreEvaluation(user.id(), restaurantId);
        model.addAttribute("preEval", preEval);

        return "evaluation";
    }


    // 2. 평가하기
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PostMapping("/web/api/evaluation/{restaurantId}")
    public ResponseEntity<String> evaluationDBcreate(
            @PathVariable Integer restaurantId,
            @AuthUser AuthUserInfo user,
            @RequestParam("starRating") Double starRating,
            @RequestParam(value = "selectedSituations", required = false) String selectedSituationsJson,
            @RequestParam(value = "evaluationComment", required = false) String evaluationComment,
            @RequestPart(value = "newImage", required = false) MultipartFile newImage
    ) {
        // JSON 문자열을 Java List로 변환
        List<Long> situations = new Gson().fromJson(selectedSituationsJson, new TypeToken<List<Long>>(){}.getType());

        // 받은 파라미터로 평가 데이터를 생성
        EvaluationDTO evaluationDTO = new EvaluationDTO(
                starRating,
                situations,
                evaluationComment,
                newImage
        );

        // 평가 데이터 저장 또는 업데이트
        evaluationCommandService.evaluate(user.id(), restaurantId, evaluationDTO);

        return ResponseEntity.ok("평가가 성공적으로 저장되었습니다.");
    }


}