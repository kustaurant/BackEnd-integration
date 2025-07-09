package com.kustaurant.kustaurant.evaluation.comment.controller.port;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentReactionResponse;

public interface EvalCommUserReactionService {
    EvalCommentReactionResponse toggleReaction(Long userId, Long commentId, ReactionType target);
}
