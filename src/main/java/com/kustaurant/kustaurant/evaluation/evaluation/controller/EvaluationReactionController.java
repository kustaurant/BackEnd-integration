package com.kustaurant.kustaurant.evaluation.evaluation.controller;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationReactionService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
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
public class EvaluationReactionController {
    private final EvaluationReactionService evalUserReactionService;

    // 1. 평가 좋아요/싫어요 토글
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PutMapping("/web/api/restaurants/evaluations/{evaluationId}/reaction")
    public ResponseEntity<EvalReactionResponse> toggleEvaluationReaction(
            @PathVariable Long evaluationId,
            @RequestParam(required = false) ReactionType reaction,
            @AuthUser AuthUserInfo user
    ) {
        EvalReactionResponse response = evalUserReactionService.setEvaluationReaction(user.id(), evaluationId, reaction);

        return ResponseEntity.ok(response);
    }
}
