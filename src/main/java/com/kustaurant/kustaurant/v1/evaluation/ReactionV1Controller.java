package com.kustaurant.kustaurant.v1.evaluation;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.evaluation.comment.controller.port.EvalCommentReactionService;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentReactionResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationReactionService;
import com.kustaurant.kustaurant.evaluation.review.ReviewQueryService;
import com.kustaurant.kustaurant.evaluation.review.ReviewsResponse;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.v1.evaluation.response.RestaurantCommentDTO;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReactionV1Controller {

    private final EvaluationReactionService evaluationReactionService;
    private final EvalCommentReactionService evalCommUserReactionService;

    // 리뷰 추천하기
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/like")
    public ResponseEntity<RestaurantCommentDTO> likeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @AuthUser AuthUserInfo user
    ) {
        // 평가인 경우
        if (!isSubComment(commentId)) { // 평가 댓글인 경우
            int evalId = commentId - EvaluationV1Controller.EVALUATION_ID_OFFSET;
            EvalReactionResponse response = evaluationReactionService.setEvaluationReaction(user.id(), (long) evalId, ReactionType.LIKE);

            return ResponseEntity.ok(RestaurantCommentDTO.fromV2(response));
        } else { // 평가에 대한 댓글인 경우
            EvalCommentReactionResponse response = evalCommUserReactionService.setEvalCommentReaction(user.id(), (long) commentId, ReactionType.LIKE);

            return ResponseEntity.ok(RestaurantCommentDTO.fromV2(response));
        }
    }

    private boolean isSubComment(Integer id) {
        return id <= EvaluationV1Controller.EVALUATION_ID_OFFSET;
    }
}
