package com.kustaurant.kustaurant.evaluation.comment.controller.web;

import com.kustaurant.kustaurant.evaluation.comment.controller.port.EvalCommCommandService;
import com.kustaurant.kustaurant.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentResponse;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class EvalCommentWebController {

    private final EvalCommCommandService evalCommCommandService;
    private final UserService userService;

    // 1. 식당평가 댓글 작성
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PostMapping("/web/api/restaurants/{restaurantId}/comments/{evalCommentId}")
    public ResponseEntity<EvalCommentResponse> postRestaurantComment(
            @PathVariable Integer restaurantId,
            @PathVariable Long evalCommentId,
            @Valid @RequestBody EvalCommentRequest req,
            @AuthUser AuthUserInfo user
    ) {
        EvalComment evalComment = evalCommCommandService.create(evalCommentId, restaurantId, user.id(), req);
        User currentUser = userService.getUserById(user.id());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(EvalCommentResponse.from(evalComment,currentUser,null, user.id()));

    }

    // 2. 식당평가 댓글 삭제
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @DeleteMapping("/web/api/restaurants/{restaurantId}/comments/{evalCommentId}")
    public ResponseEntity<Void> deleteRestaurantComment(
            @PathVariable Long evalCommentId,
            @PathVariable Integer restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        evalCommCommandService.delete(evalCommentId, restaurantId, user.id());

        return ResponseEntity.noContent().build();
    }
}
