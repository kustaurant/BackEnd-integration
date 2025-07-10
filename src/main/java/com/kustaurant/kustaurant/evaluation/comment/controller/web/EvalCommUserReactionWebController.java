package com.kustaurant.kustaurant.evaluation.comment.controller.web;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentReactionResponse;
import com.kustaurant.kustaurant.evaluation.comment.service.EvalCommUserReactionServiceImpl;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class EvalCommUserReactionWebController {
    private final EvalCommUserReactionServiceImpl evalCommUserReactionService;


    // 1. 식당평가 댓글 좋아요
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PostMapping("/web/api/restaurants/comments/{evalCommentId}/{reaction}")
    public ResponseEntity<EvalCommentReactionResponse> toggleReaction(
            @PathVariable Long evalCommentId,
            @PathVariable String reaction,
            @AuthUser AuthUserInfo user
    ) {
        ReactionType reactionType = ReactionType.valueOf(reaction);
        EvalCommentReactionResponse response = evalCommUserReactionService.toggleReaction(user.id(), evalCommentId, reactionType);

        return ResponseEntity.ok(response);
    }


}
