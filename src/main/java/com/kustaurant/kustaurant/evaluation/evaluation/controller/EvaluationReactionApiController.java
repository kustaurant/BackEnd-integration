package com.kustaurant.kustaurant.evaluation.evaluation.controller;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationReactionService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EvaluationReactionApiController implements EvaluationReactionApiDoc {
    private final EvaluationReactionService evalUserReactionService;


    // 1. 평가 좋아요/싫어요 토글
    @PostMapping("/v2/auth/restaurants/evaluations/{evaluationId}/{reaction}")
    public ResponseEntity<EvalReactionResponse> toggleEvaluationReaction(
            @PathVariable Long evaluationId,
            @PathVariable String reaction,
            @AuthUser AuthUserInfo user
    ) {
        ReactionType reactionType = ReactionType.valueOf(reaction);
        EvalReactionResponse response = evalUserReactionService.toggleReaction(user.id(), evaluationId, reactionType);

        return ResponseEntity.ok(response);
    }
}
