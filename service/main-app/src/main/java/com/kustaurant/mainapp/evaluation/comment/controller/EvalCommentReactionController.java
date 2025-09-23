package com.kustaurant.mainapp.evaluation.comment.controller;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.evaluation.comment.controller.port.EvalCommentReactionService;
import com.kustaurant.mainapp.evaluation.comment.controller.response.EvalCommentReactionResponse;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EvalCommentReactionController {
    private final EvalCommentReactionService evalCommUserReactionService;


    // 1. 식당평가 댓글 좋아요/싫어요
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PutMapping("/web/api/restaurants/comments/{evalCommentId}/reaction")
    public ResponseEntity<EvalCommentReactionResponse> toggleReaction(
            @PathVariable Long evalCommentId,
            @RequestParam(required = false) ReactionType reaction,
            @AuthUser AuthUserInfo user
    ) {
        EvalCommentReactionResponse response = evalCommUserReactionService.setEvalCommentReaction(user.id(), evalCommentId, reaction);

        return ResponseEntity.ok(response);
    }
}
