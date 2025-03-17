package com.kustaurant.kustaurant.web.restaurant.controller;

import com.kustaurant.kustaurant.common.evaluation.constants.EvaluationConstants;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantCommentDTO;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Evaluation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantComment;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantMenu;
import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantCommentService;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantFavoriteService;
import com.kustaurant.kustaurant.web.restaurant.service.RestaurantWebService;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.tab3_tier.etc.EnumSortComment;
import com.kustaurant.kustaurant.tab3_tier.etc.RestaurantTierDataClass;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.repository.EvaluationRepository;
import com.kustaurant.kustaurant.global.webUser.CustomOAuth2UserService;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class RestaurantWebController {
    private final RestaurantCommentService restaurantCommentService;
    private final EvaluationService evaluationService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final RestaurantWebService restaurantWebService;
    private final RestaurantFavoriteService restaurantFavoriteService;
    private final EvaluationRepository evaluationRepository;

    private static final Logger logger = LoggerFactory.getLogger(RestaurantWebController.class);
    
    @Value("${restaurant.initialDisplayMenuCount}")
    private int initialDisplayMenuCount;

    @GetMapping("/restaurants/{restaurantId}")
    public String restaurant(
            Model model,
            @PathVariable Integer restaurantId,
            Principal principal
    ) {
        // 식당 정보
        Restaurant restaurant = restaurantWebService.getRestaurant(restaurantId);
        restaurantWebService.plusVisitCount(restaurant);
        model.addAttribute("restaurant", restaurant);
        String visitCountData = restaurant.getVisitCount() +
                "회";
//        (상위 " + Math.round(restaurantService.getPercentOrderByVisitCount(restaurant) * 100.0) / 100.0 + "%)";
        model.addAttribute("visitCountData", visitCountData);
        model.addAttribute("evaluationCountData", evaluationRepository.countByRestaurant(restaurant));
        model.addAttribute("favoriteCount", restaurantFavoriteService.getFavoriteCountByRestaurant(restaurant));
        // 티어 정보
        RestaurantTierDataClass restaurantTierDataClass = new RestaurantTierDataClass(restaurant);
        evaluationService.insertSituation(restaurantTierDataClass, restaurant);
        model.addAttribute("situationTierList", restaurantTierDataClass.getRestaurantSituationRelationList());
        // 메뉴
        List<RestaurantMenu> restaurantMenus = restaurantWebService.getRestaurantMenuList(restaurantId);
        model.addAttribute("menus", restaurantMenus);
        // 메뉴 펼치기 이전에 몇개의 메뉴를 보여줄 것인가
        model.addAttribute("initialDisplayMenuCount", initialDisplayMenuCount);

        // 평가하기 버튼
        // 로그인 안되어있을 경우
        if (principal == null) {
            // 식당 댓글
//            List<Object[]> restaurantComments = restaurantCommentService.getCommentList(restaurantId, EnumSortComment.POPULAR);
//            model.addAttribute("restaurantComments", restaurantComments);
            List<RestaurantCommentDTO> restaurantComments = restaurantCommentService.getRestaurantCommentList(restaurant, null, true, "ios");
            model.addAttribute("restaurantComments", restaurantComments);
            //
            model.addAttribute("evaluationButton", " 평가하기");
        } else {
            // 유저
            User user = customOAuth2UserService.getUser(principal.getName());
            model.addAttribute("user", user);
            // 식당 댓글
//            List<Object[]> restaurantComments = restaurantCommentService.getCommentList(restaurantId, EnumSortComment.POPULAR, user);
//            model.addAttribute("restaurantComments", restaurantComments);
            List<RestaurantCommentDTO> restaurantComments = restaurantCommentService.getRestaurantCommentList(restaurant, user, true, "ios");
            model.addAttribute("restaurantComments", restaurantComments);
            // 즐겨찾기 여부
            model.addAttribute("isFavoriteExist", restaurantFavoriteService.isFavoriteExist(principal.getName(), restaurantId));
            //
            Evaluation evaluation = evaluationService.getByUserAndRestaurant(user, restaurant);
            if (evaluation!=null) {
                model.addAttribute("evaluationButton", "다시 평가하기");
            } else {
                model.addAttribute("evaluationButton", " 평가하기");
            }
        }
        return "restaurant";
    }

    // 해당 cuisine에 맞는 식당 목록 반환
    @GetMapping("/api/restaurants")
    public ResponseEntity<List<Restaurant>> getRestaurantsByCuisine(
            @RequestParam(value = "cuisine", defaultValue = "전체") String cuisine
    ) {
        List<Restaurant> restaurants = restaurantWebService.getRestaurantList(cuisine);

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }


    // 식당 메뉴 반환
    @GetMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<List<RestaurantMenu>> getRestaurantMenusByRestaurantId(
            @PathVariable Integer restaurantId
    ) {
        //TODO: 반환값이 null일 경우(해당 식당의 status가 ACTIVE가 아닐 경우) 처리 해줘야함.
        List<RestaurantMenu> restaurantMenus = restaurantWebService.getRestaurantMenuList(restaurantId);

        return new ResponseEntity<>(restaurantMenus, HttpStatus.OK);
    }

    // 식당 댓글 작성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/restaurants/{restaurantId}/comments")
    public ResponseEntity<String> postRestaurantComment(
            @PathVariable Integer restaurantId,
            @RequestBody Map<String, Object> jsonBody,
            Principal principal
    ) {
        String commentBody = jsonBody.get("commentBody").toString();
        if (commentBody.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("내용을 입력해 주세요.");
        }
        String result = restaurantCommentService.addComment(
                restaurantId,
                principal.getName(),
                commentBody);
        if (result.equals("ok")) {
            return ResponseEntity.ok("Comment added successfully");
        } else if (result.equals("userTokenId")) {
            return ResponseEntity.ok("UserTokenId doesn't exist");
        } else {
            return ResponseEntity.ok("what");
        }
    }

    // 식당 댓글 목록 html 로드
    @GetMapping("/api/restaurants/{restaurantId}/comments")
    public String getRestaurantCommentByRestaurantId(
            @PathVariable Integer restaurantId,
            @RequestParam(value = "sort", defaultValue = "POPULAR") String sort,
            Principal principal,
            Model model
    ) {
        EnumSortComment sortComment = EnumSortComment.valueOf(sort);
        Restaurant restaurant = restaurantWebService.getRestaurant(restaurantId);

        if (principal == null) {
            List<RestaurantCommentDTO> restaurantComments = restaurantCommentService.getRestaurantCommentList(restaurant, null, sortComment.equals(EnumSortComment.POPULAR), "ios");
            model.addAttribute("restaurantComments", restaurantComments);
        } else {
            User user = customOAuth2UserService.getUser(principal.getName());

            model.addAttribute("user", user);

            List<RestaurantCommentDTO> restaurantComments = restaurantCommentService.getRestaurantCommentList(restaurant, user, sortComment.equals(EnumSortComment.POPULAR), "ios");
            model.addAttribute("restaurantComments", restaurantComments);
        }

        return "restaurantComments";
    }

    // 식당 댓글 하나 html 로드
    @GetMapping("/api/restaurants/comments/{evaluationId}")
    public String getARenderedRestaurantCommentHtml(
            Model model,
            @PathVariable Integer evaluationId,
            Principal principal
    ) {
        evaluationId -= EvaluationConstants.EVALUATION_ID_OFFSET;

        if (principal != null) {
            User user = customOAuth2UserService.getUser(principal.getName());

            model.addAttribute("user", user);

            RestaurantCommentDTO restaurantCommentDTO = restaurantCommentService.getRestaurantCommentDTO(evaluationId, user, "ios");
            model.addAttribute("restaurantComment", restaurantCommentDTO);
        } else {
            RestaurantCommentDTO restaurantCommentDTO = restaurantCommentService.getRestaurantCommentDTO(evaluationId, null, "ios");
            model.addAttribute("restaurantComment", restaurantCommentDTO);

            model.addAttribute("isLiked", false);
            model.addAttribute("isHated", false);
        }

        return "restaurantComment";
    }

    // 식당 댓글 좋아요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/restaurants/comments/{commentId}/like")
    public ResponseEntity<Map<String, String>> likeRestaurantComment(
            @PathVariable Integer commentId,
            Principal principal
    ) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            User user = customOAuth2UserService.getUser(principal.getName());
            if (isSubComment(commentId)) { // 대댓글인 경우
                RestaurantComment restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
                restaurantCommentService.likeComment(user, restaurantComment, responseMap);
            } else { // 부모 댓글인 경우
                int evaluationId = commentId - EvaluationConstants.EVALUATION_ID_OFFSET;
                Evaluation evaluation = evaluationService.getByEvaluationId(evaluationId);
                evaluationService.likeEvaluation(user, evaluation);
            }
        } catch (DataNotFoundException e) {
            logger.error("RestaurantCommentLikeError", e);
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
        } catch (IllegalStateException e) {
            logger.error("RestaurantCommentLikeError", e);
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
        return ResponseEntity.ok(responseMap);
    }

    // 식당 댓글 싫어요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/restaurants/comments/{commentId}/dislike")
    public ResponseEntity<Map<String, String>> dislikeRestaurantComment(
            @PathVariable Integer commentId,
            Principal principal
    ) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            User user = customOAuth2UserService.getUser(principal.getName());
            if (isSubComment(commentId)) { // 대댓글인 경우
                RestaurantComment restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
                restaurantCommentService.dislikeComment(user, restaurantComment, responseMap);
            } else { // 부모 댓글인 경우
                int evaluationId = commentId - EvaluationConstants.EVALUATION_ID_OFFSET;
                Evaluation evaluation = evaluationService.getByEvaluationId(evaluationId);
                evaluationService.dislikeEvaluation(user, evaluation);
            }
        } catch (DataNotFoundException e) {
            logger.error("RestaurantCommentDislikeError", e);
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
        } catch (IllegalStateException e) {
            logger.error("RestaurantCommentDislikeError", e);
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
        return ResponseEntity.ok(responseMap);
    }

    // 식당 댓글 삭제
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @DeleteMapping("/api/restaurants/comments/{commentId}")
    public ResponseEntity<String> deleteRestaurantComment(
            @PathVariable Integer commentId,
            Principal principal
            ) {
        User user = customOAuth2UserService.getUser(principal.getName());
        if (isSubComment(commentId)) {
            RestaurantComment restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
            // 삭제 요청한 user가 댓글을 단 user가 아닌 경우
            if (!restaurantComment.getUser().equals(user)) {
                return ResponseEntity.badRequest().build();
            }
            // 대댓글 지우기
            restaurantCommentService.deleteComment(restaurantComment, user);
        } else {
            int evaluationId = commentId - EvaluationConstants.EVALUATION_ID_OFFSET;
            Evaluation evaluation = evaluationService.getByEvaluationId(evaluationId);
            if (!evaluation.getUser().equals(user)) {
                return ResponseEntity.badRequest().build();
            }
            // 댓글 내용 지우기
            evaluationService.deleteComment(evaluation, user);
        }

        return ResponseEntity.ok("success");
    }

    // 식당 즐겨찾기
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/restaurants/{restaurantId}/favorite/toggle")
    public ResponseEntity<Boolean> toggleFavorite(
            @PathVariable Integer restaurantId,
            Principal principal
    ) {
        return ResponseEntity.ok(restaurantFavoriteService.toggleFavorite(principal.getName(), restaurantId));
    }

    private boolean isSubComment(Integer id) {
        return id <= EvaluationConstants.EVALUATION_ID_OFFSET;
    }
}