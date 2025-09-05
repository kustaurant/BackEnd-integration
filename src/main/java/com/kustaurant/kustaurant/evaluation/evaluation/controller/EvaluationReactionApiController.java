package com.kustaurant.kustaurant.evaluation.evaluation.controller;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationReactionService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EvaluationReactionApiController {
    private final EvaluationReactionService evalUserReactionService;


    // 1. 평가 좋아요/싫어요 토글
    @Operation(
            summary = "평가 좋아요/싫어요 토글",
            description = "반응을 누른 후의 좋아요 수/싫어요 수 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 후의 추천/비추천 수를 반환해줍니다.", content = {@Content(schema = @Schema(implementation = EvalReactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "해당 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없거나 commentId에 해당하는 comment가 없는 경우 404를 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "없는 경우겠지만 만에 하나 DB 일관성에 문제가 생겼을 경우 500을 반환하게 했습니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
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
