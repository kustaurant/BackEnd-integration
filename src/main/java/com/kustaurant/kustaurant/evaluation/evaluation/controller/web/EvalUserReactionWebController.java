package com.kustaurant.kustaurant.evaluation.evaluation.controller.web;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvalUserReactionService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EvalUserReactionWebController {
    private final EvalUserReactionService evalUserReactionService;

    // 1. 평가 좋아요/싫어요 토글
    @PostMapping("/web/api/restaurants/evaluations/{evaluationId}/{reaction}")
    public ResponseEntity<Map<String, String>> likeRestaurantComment(
            @AuthUser AuthUserInfo user,
            @PathVariable Long evaluationId,
            @PathVariable String reaction,
            Model model
    ) {
        ReactionType reactionType = ReactionType.valueOf(reaction);
        EvalReactionResponse response = evalUserReactionService.toggleReaction(user.id(), evaluationId, reactionType);

        model.addAttribute("evaluationId", response.evaluationId());
        model.addAttribute("reaction", response.reaction());
        model.addAttribute("reaction", response.likeCount());
        model.addAttribute("reaction", response.dislikeCount());

        return ResponseEntity.ok().build();
    }
}
