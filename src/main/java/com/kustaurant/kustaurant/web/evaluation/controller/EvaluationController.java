    package com.kustaurant.kustaurant.web.evaluation.controller;

    import com.google.gson.Gson;
    import com.google.gson.reflect.TypeToken;
    import com.kustaurant.kustaurant.api.restaurant.controller.MainController;
    import com.kustaurant.kustaurant.common.evaluation.domain.EvaluationDTO;
    import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Evaluation;
    import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
    import com.kustaurant.kustaurant.common.restaurant.infrastructure.repository.EvaluationRepository;
    import com.kustaurant.kustaurant.global.webUser.CustomOAuth2UserService;
    import com.kustaurant.kustaurant.web.restaurant.service.RestaurantWebService;
    import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;

    import com.kustaurant.kustaurant.common.user.infrastructure.User;
    import lombok.RequiredArgsConstructor;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.security.Principal;
    import java.util.List;

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
            Restaurant restaurant = restaurantWebService.getRestaurant(restaurantId);
            User user = customOAuth2UserService.getUser(principal.getName());
            Evaluation evaluation = evaluationRepository.findByUserAndRestaurant(user, restaurant).orElse(null);
            Double mainScore = 0.0;
            model.addAttribute("restaurant", restaurant);

            if (evaluation!=null) {
                model.addAttribute("eval",evaluation);
                model.addAttribute("mainScore", evaluation.getEvaluationScore());
                if(evaluation.getSituationIdList()!=null){
                    model.addAttribute("situationJson",evaluation.getSituationIdList());
                }
            }
            else{
                return "evaluation";
            }
            return "evaluation";
        }
        // 평가 데이터 db 저장 (기존 평가 존재 시 업데이트 진행)
        @PreAuthorize("isAuthenticated() and hasRole('USER')")
        @PostMapping("/api/evaluation/{restaurantId}")
        public ResponseEntity<?> evaluationDBcreate(
                @PathVariable Integer restaurantId,
                Principal principal,
                @RequestParam("starRating") Double starRating,
                @RequestParam(value = "selectedSituations", required = false) String selectedSituationsJson,
                @RequestParam(value = "evaluationComment", required = false) String evaluationComment,
                @RequestPart(value = "newImage", required = false) MultipartFile newImage
        ) {
            User user = customOAuth2UserService.getUser(principal.getName());
            Restaurant restaurant = restaurantWebService.getRestaurant(restaurantId);
            // JSON 문자열을 Java List로 변환
            List<Integer> evaluationSituations = new Gson().fromJson(selectedSituationsJson, new TypeToken<List<Integer>>(){}.getType());

            // 받은 파라미터로 평가 데이터를 생성
            EvaluationDTO evaluationDTO = new EvaluationDTO();
            evaluationDTO.setEvaluationScore(starRating);
            evaluationDTO.setEvaluationSituations(evaluationSituations);
            evaluationDTO.setEvaluationComment(evaluationComment);
            evaluationDTO.setNewImage(newImage);

            // 평가 데이터 저장 또는 업데이트
            evaluationService.createOrUpdate(user, restaurant, evaluationDTO);

            return ResponseEntity.ok("평가가 성공적으로 저장되었습니다.");
        }




    }
