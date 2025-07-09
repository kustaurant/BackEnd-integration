package com.kustaurant.kustaurant.evaluation.comment.controller.web;

import com.kustaurant.kustaurant.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.evaluation.comment.service.EvalCommCommandServiceImpl;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class EvalCommentWebController {
    private final EvalCommCommandServiceImpl evalCommCommandService;

    // 1. 식당평가 댓글 작성
    @PostMapping("/web/api/restaurants/{restaurantId}/comments/{evalCommentId}")
    public ResponseEntity<String> postRestaurantComment(
            @PathVariable Integer restaurantId,
            @PathVariable Long evalCommentId,
            @Valid @RequestBody EvalCommentRequest req,
            @AuthUser AuthUserInfo user,
            Model model
    ) {
        EvalComment evalComment = evalCommCommandService.create(evalCommentId, restaurantId, user.id(), req);

        return ResponseEntity.ok("Comment added successfully");

    }


//    // 2. 식당 댓글 목록 html 로드
//    @GetMapping("/web/api/restaurants/{restaurantId}/comments")
//    public String getRestaurantCommentByRestaurantId(
//            @PathVariable Integer restaurantId,
//            @RequestParam(value = "sort", defaultValue = "POPULAR") String sort,
//            @AuthUser AuthUserInfo user,
//            Model model
//    ) {
//        EnumSortComment sortComment = EnumSortComment.valueOf(sort);
//
//        if (user.id() == null) {
//            List<EvalCommResponse> restaurantComments = restaurantCommentService.getRestaurantCommentList(restaurantId, null, sortComment.equals(EnumSortComment.POPULAR));
//            model.addAttribute("restaurantComments", restaurantComments);
//        } else {
//            User writer = userService.getUserById(user.id());
//            model.addAttribute("user", writer);
//
//            List<EvalCommResponse> restaurantComments = restaurantCommentService.getRestaurantCommentList(restaurantId, user.id(), sortComment.equals(EnumSortComment.POPULAR));
//            model.addAttribute("restaurantComments", restaurantComments);
//        }
//
//        return "restaurantComments";
//    }
//
//
//    // 3. 식당 댓글 하나 html 로드
//    @GetMapping("/web/api/restaurants/comments/{evalId}")
//    public String getARenderedRestaurantCommentHtml(
//            Model model,
//            @PathVariable Integer evalId,
//            @AuthUser AuthUserInfo user
//    ) {
//        evalId -= EvaluationConstants.EVALUATION_ID_OFFSET;
//
//        if (user.id() != null) {
//            User writer = userService.getUserById(user.id());
//            model.addAttribute("user", writer);
//
//            EvalCommResponse restaurantCommentDTO = restaurantCommentService.getRestaurantCommentDTO(evalId, user.id());
//            model.addAttribute("restaurantComment", restaurantCommentDTO);
//        } else {
//            EvalCommResponse restaurantCommentDTO = restaurantCommentService.getRestaurantCommentDTO(evalId, null);
//            model.addAttribute("restaurantComment", restaurantCommentDTO);
//
//            model.addAttribute("isLiked", false);
//            model.addAttribute("isHated", false);
//        }
//
//        return "restaurantComment";
//    }


    // 4. 식당평가 댓글 삭제
    @DeleteMapping("/web/api/restaurants/{restaurantId}/comments/{evalCommentId}")
    public ResponseEntity<String> deleteRestaurantComment(
            @PathVariable Long evalCommentId,
            @PathVariable Integer restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        evalCommCommandService.delete(evalCommentId, restaurantId, user.id());

        return ResponseEntity.ok("success");
    }
}
