package com.kustaurant.kustaurant.evaluation.evaluation.controller;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationReactionService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EvaluationReactionApiController implements EvaluationReactionApiDoc {
    private final EvaluationReactionService evaluationReactionService;

    // 1. 평가 좋아요/싫어요
    @PutMapping("/v2/auth/restaurants/evaluations/{evaluationId}/reaction")
    public ResponseEntity<EvalReactionResponse> setEvaluationReactionApi(
            @PathVariable Long evaluationId,
            @RequestParam(required = false) ReactionType reaction,
            @AuthUser AuthUserInfo user
    ) {
        EvalReactionResponse response = evaluationReactionService.setEvaluationReaction(user.id(), evaluationId, reaction);

        return ResponseEntity.ok(response);
    }
}
