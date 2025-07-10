package com.kustaurant.kustaurant.evaluation.comment.controller.web;

import com.kustaurant.kustaurant.evaluation.comment.controller.port.EvalCommCommandService;
import com.kustaurant.kustaurant.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
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
    private final EvalCommCommandService evalCommCommandService;

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

    // 2. 식당평가 댓글 삭제
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
