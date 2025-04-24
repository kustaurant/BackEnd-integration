    package com.kustaurant.kustaurant.web.evaluation.controller;

    import com.google.gson.Gson;
    import com.google.gson.reflect.TypeToken;
    import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
    import com.kustaurant.kustaurant.web.restaurant.MainController;
    import com.kustaurant.kustaurant.common.evaluation.constants.EvaluationConstants;
    import com.kustaurant.kustaurant.common.evaluation.domain.EvaluationDTO;
    import com.kustaurant.kustaurant.common.evaluation.infrastructure.RestaurantComment;
    import com.kustaurant.kustaurant.common.evaluation.service.port.EvaluationRepository;
    import com.kustaurant.kustaurant.common.evaluation.infrastructure.EvaluationEntity;
    import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantCommentDTO;
    import com.kustaurant.kustaurant.common.restaurant.domain.enums.EnumSortComment;
    import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
    import com.kustaurant.kustaurant.common.restaurant.service.RestaurantCommentService;
    import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
    import com.kustaurant.kustaurant.global.auth.webUser.CustomOAuth2UserService;
    import com.kustaurant.kustaurant.web.restaurant.RestaurantWebService;
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
    import org.springframework.web.multipart.MultipartFile;

    import java.security.Principal;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @RequiredArgsConstructor
    @Controller
    public class EvaluationController {
        private final RestaurantWebService restaurantWebService;
        private final EvaluationService evaluationService;
        private final CustomOAuth2UserService customOAuth2UserService;
        private final EvaluationRepository evaluationRepository;
        private final RestaurantCommentService restaurantCommentService;

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
            RestaurantEntity restaurant = restaurantWebService.getRestaurant(restaurantId);
            UserEntity UserEntity = customOAuth2UserService.getUser(principal.getName());
            EvaluationEntity evaluation = evaluationRepository.findByUserAndRestaurant(UserEntity, restaurant).orElse(null);
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
            UserEntity UserEntity = customOAuth2UserService.getUser(principal.getName());
            RestaurantEntity restaurant = restaurantWebService.getRestaurant(restaurantId);
            // JSON 문자열을 Java List로 변환
            List<Integer> evaluationSituations = new Gson().fromJson(selectedSituationsJson, new TypeToken<List<Integer>>(){}.getType());

            // 받은 파라미터로 평가 데이터를 생성
            EvaluationDTO evaluationDTO = new EvaluationDTO();
            evaluationDTO.setEvaluationScore(starRating);
            evaluationDTO.setEvaluationSituations(evaluationSituations);
            evaluationDTO.setEvaluationComment(evaluationComment);
            evaluationDTO.setNewImage(newImage);

            // 평가 데이터 저장 또는 업데이트
            evaluationService.createOrUpdate(UserEntity, restaurant, evaluationDTO);

            return ResponseEntity.ok("평가가 성공적으로 저장되었습니다.");
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

            if (principal == null) {
                List<RestaurantCommentDTO> restaurantComments = restaurantCommentService.getRestaurantCommentList(restaurantId, null, sortComment.equals(EnumSortComment.POPULAR), "ios");
                model.addAttribute("restaurantComments", restaurantComments);
            } else {
                UserEntity UserEntity = customOAuth2UserService.getUser(principal.getName());

                model.addAttribute("user", UserEntity);

                List<RestaurantCommentDTO> restaurantComments = restaurantCommentService.getRestaurantCommentList(restaurantId, UserEntity, sortComment.equals(EnumSortComment.POPULAR), "ios");
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
                UserEntity UserEntity = customOAuth2UserService.getUser(principal.getName());

                model.addAttribute("user", UserEntity);

                RestaurantCommentDTO restaurantCommentDTO = restaurantCommentService.getRestaurantCommentDTO(evaluationId, UserEntity, "ios");
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
                UserEntity UserEntity = customOAuth2UserService.getUser(principal.getName());
                if (isSubComment(commentId)) { // 대댓글인 경우
                    RestaurantComment restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
                    restaurantCommentService.likeComment(UserEntity, restaurantComment, responseMap);
                } else { // 부모 댓글인 경우
                    int evaluationId = commentId - EvaluationConstants.EVALUATION_ID_OFFSET;
                    EvaluationEntity evaluation = evaluationService.getByEvaluationId(evaluationId);
                    evaluationService.likeEvaluation(UserEntity, evaluation);
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
                UserEntity UserEntity = customOAuth2UserService.getUser(principal.getName());
                if (isSubComment(commentId)) { // 대댓글인 경우
                    RestaurantComment restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
                    restaurantCommentService.dislikeComment(UserEntity, restaurantComment, responseMap);
                } else { // 부모 댓글인 경우
                    int evaluationId = commentId - EvaluationConstants.EVALUATION_ID_OFFSET;
                    EvaluationEntity evaluation = evaluationService.getByEvaluationId(evaluationId);
                    evaluationService.dislikeEvaluation(UserEntity, evaluation);
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
            UserEntity UserEntity = customOAuth2UserService.getUser(principal.getName());
            if (isSubComment(commentId)) {
                RestaurantComment restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
                // 삭제 요청한 user가 댓글을 단 user가 아닌 경우
                if (!restaurantComment.getUser().equals(UserEntity)) {
                    return ResponseEntity.badRequest().build();
                }
                // 대댓글 지우기
                restaurantCommentService.deleteComment(restaurantComment, UserEntity);
            } else {
                int evaluationId = commentId - EvaluationConstants.EVALUATION_ID_OFFSET;
                EvaluationEntity evaluation = evaluationService.getByEvaluationId(evaluationId);
                if (!evaluation.getUser().equals(UserEntity)) {
                    return ResponseEntity.badRequest().build();
                }
                // 댓글 내용 지우기
                evaluationService.deleteComment(evaluation, UserEntity);
            }

            return ResponseEntity.ok("success");
        }


        private boolean isSubComment(Integer id) {
            return id <= EvaluationConstants.EVALUATION_ID_OFFSET;
        }


    }
