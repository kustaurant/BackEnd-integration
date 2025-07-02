package com.kustaurant.kustaurant.evaluation.evaluation.controller.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationCommandService;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationQueryService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.evaluation.comment.service.EvaluationCommentService;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantService;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantWebService;
import com.kustaurant.kustaurant.evaluation.evaluation.constants.EvaluationConstants;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDTO;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EvaluationWebController {
    private final RestaurantWebService restaurantWebService;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationCommentService restaurantCommentService;

    private final RestaurantService restaurantService;
    private final EvaluationService evaluationService;

    private final EvaluationQueryService evaluationQueryService;
    private final EvaluationCommandService evaluationCommandService;

    // 평가 페이지 화면
    @GetMapping("/evaluation/{restaurantId}")
    public String evaluation(
            Model model,
            @AuthUser AuthUserInfo user,
            @PathVariable Integer restaurantId
    ) {
        // 식당 정보
        Restaurant restaurant = restaurantService.getActiveDomain(restaurantId);
        model.addAttribute("restaurant", restaurant);
        // 이전 평가 정보
        EvaluationDTO preEval = evaluationQueryService.getPreEvaluation(user.id(), restaurantId);
        model.addAttribute("preEval", preEval);

        return "evaluation";
    }

    // 평가하기
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

//        // 식당 댓글 작성
//        @PostMapping("/api/restaurants/{restaurantId}/comments")
//        public ResponseEntity<String> postRestaurantComment(
//                @PathVariable Integer restaurantId,
//                @RequestBody Map<String, Object> jsonBody,
//                @AuthUser AuthUserInfo user
//        ) {
//            String commentBody = jsonBody.get("commentBody").toString();
//            if (commentBody.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("내용을 입력해 주세요.");
//            }
//            String result = restaurantCommentService.addComment(
//                    restaurantId,
//                    user.id(),
//                    commentBody);
//            if (result.equals("ok")) {
//                return ResponseEntity.ok("Comment added successfully");
//            } else if (result.equals("userTokenId")) {
//                return ResponseEntity.ok("UserTokenId doesn't exist");
//            } else {
//                return ResponseEntity.ok("what");
//            }
//        }
//
//        // 식당 댓글 목록 html 로드
//        @GetMapping("/api/restaurants/{restaurantId}/comments")
//        public String getRestaurantCommentByRestaurantId(
//                @PathVariable Integer restaurantId,
//                @RequestParam(value = "sort", defaultValue = "POPULAR") String sort,
//                @AuthUser AuthUserInfo user,
//                Model model
//        ) {
//            EnumSortComment sortComment = EnumSortComment.valueOf(sort);
//
//            if (user.id() == null) {
//                List<EvalCommResponse> restaurantComments = restaurantCommentService.getRestaurantCommentList(restaurantId, null, sortComment.equals(EnumSortComment.POPULAR));
//                model.addAttribute("restaurantComments", restaurantComments);
//            } else {
//                User writer = userService.getUserById(user.id());
//                model.addAttribute("user", writer);
//
//                List<EvalCommResponse> restaurantComments = restaurantCommentService.getRestaurantCommentList(restaurantId, user.id(), sortComment.equals(EnumSortComment.POPULAR));
//                model.addAttribute("restaurantComments", restaurantComments);
//            }
//
//            return "restaurantComments";
//        }

    // 식당 댓글 하나 html 로드
//        @GetMapping("/api/restaurants/comments/{evaluationId}")
//        public String getARenderedRestaurantCommentHtml(
//                Model model,
//                @PathVariable Integer evaluationId,
//                @AuthUser AuthUserInfo user
//        ) {
//            evaluationId -= EvaluationConstants.EVALUATION_ID_OFFSET;
//
//            if (user.id() != null) {
//                User writer = userService.getUserById(user.id());
//                model.addAttribute("user", writer);
//
//                EvalCommResponse restaurantCommentDTO = restaurantCommentService.getRestaurantCommentDTO(evaluationId, user.id());
//                model.addAttribute("restaurantComment", restaurantCommentDTO);
//            } else {
//                EvalCommResponse restaurantCommentDTO = restaurantCommentService.getRestaurantCommentDTO(evaluationId, null);
//                model.addAttribute("restaurantComment", restaurantCommentDTO);
//
//                model.addAttribute("isLiked", false);
//                model.addAttribute("isHated", false);
//            }
//
//            return "restaurantComment";
//        }

    // 식당 댓글 좋아요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/restaurants/comments/{commentId}/like")
    public ResponseEntity<Map<String, String>> likeRestaurantComment(
            @PathVariable Integer commentId,
            @AuthUser AuthUserInfo user
    ) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            if (isSubComment(commentId)) { // 대댓글인 경우
                RestaurantCommentEntity restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
                restaurantCommentService.likeComment(user.id(), restaurantComment, responseMap);
            } else { // 부모 댓글인 경우
                int evaluationId = commentId - EvaluationConstants.EVALUATION_ID_OFFSET;
                EvaluationEntity evaluation = evaluationService.getByEvaluationId(evaluationId);
                evaluationService.likeEvaluation(user.id(), evaluation);
            }
        } catch (DataNotFoundException e) {
            log.error("RestaurantCommentLikeError", e);
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
        } catch (IllegalStateException e) {
            log.error("RestaurantCommentLikeError", e);
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
        return ResponseEntity.ok(responseMap);
    }

    // 식당 댓글 싫어요
    @GetMapping("/api/restaurants/comments/{commentId}/dislike")
    public ResponseEntity<Map<String, String>> dislikeRestaurantComment(
            @PathVariable Integer commentId,
            @AuthUser AuthUserInfo user
    ) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            if (isSubComment(commentId)) { // 대댓글인 경우
                RestaurantCommentEntity restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
                restaurantCommentService.dislikeComment(user.id(), restaurantComment, responseMap);
            } else { // 부모 댓글인 경우
                int evaluationId = commentId - EvaluationConstants.EVALUATION_ID_OFFSET;
                EvaluationEntity evaluation = evaluationService.getByEvaluationId(evaluationId);
                evaluationService.dislikeEvaluation(user.id(), evaluation);
            }
        } catch (DataNotFoundException e) {
            log.error("RestaurantCommentDislikeError", e);
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
        } catch (IllegalStateException e) {
            log.error("RestaurantCommentDislikeError", e);
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
        return ResponseEntity.ok(responseMap);
    }

    // 식당 댓글 삭제
    @DeleteMapping("/api/restaurants/comments/{commentId}")
    public ResponseEntity<String> deleteRestaurantComment(
            @PathVariable Integer commentId,
            @AuthUser AuthUserInfo user
    ) {

        if (isSubComment(commentId)) {
            RestaurantCommentEntity restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
            // 삭제 요청한 user가 댓글을 단 user가 아닌 경우
            if (!restaurantComment.getUserId().equals(user.id())) {
                return ResponseEntity.badRequest().build();
            }
            // 대댓글 지우기
            restaurantCommentService.deleteComment(restaurantComment, user.id());
        } else {
            int evaluationId = commentId - EvaluationConstants.EVALUATION_ID_OFFSET;
            EvaluationEntity evaluation = evaluationService.getByEvaluationId(evaluationId);
            if (!evaluation.getUserId().equals(user.id())) {
                return ResponseEntity.badRequest().build();
            }
            // 댓글 내용 지우기
            evaluationService.deleteComment(evaluation, user.id());
        }

        return ResponseEntity.ok("success");
    }


    private boolean isSubComment(Integer id) {
        return id <= EvaluationConstants.EVALUATION_ID_OFFSET;
    }


}