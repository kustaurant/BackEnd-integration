package com.kustaurant.kustaurant.evaluation.comment.controller.web;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentReactionResponse;
import com.kustaurant.kustaurant.evaluation.comment.service.EvalCommUserReactionService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class EvalCommUserReactionWebController {
    private final EvalCommUserReactionService evalCommUserReactionService;


    // 1. 식당평가 댓글 좋아요
    @GetMapping("/web/api/restaurants/comments/{evalCommentId}/{reaction}")
    public ResponseEntity<Void> likeRestaurantComment(
            @PathVariable Long evalCommentId,
            @PathVariable String reaction,
            @AuthUser AuthUserInfo user
    ) {
        ReactionType reactionType = ReactionType.valueOf(reaction);
        EvalCommentReactionResponse response = evalCommUserReactionService.toggleReaction(user.id(), evalCommentId, reactionType);

        return ResponseEntity.ok().build();
    }


}
