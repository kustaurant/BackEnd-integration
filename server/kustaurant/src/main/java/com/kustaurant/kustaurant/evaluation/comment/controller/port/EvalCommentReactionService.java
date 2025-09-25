package com.kustaurant.kustaurant.evaluation.comment.controller.port;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentReactionResponse;

public interface EvalCommentReactionService {
    EvalCommentReactionResponse setEvalCommentReaction(Long userId, Long evalCommentId, ReactionType target);
}
