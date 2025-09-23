package com.kustaurant.mainapp.evaluation.comment.controller.port;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.evaluation.comment.controller.response.EvalCommentReactionResponse;

public interface EvalCommentReactionService {
    EvalCommentReactionResponse setEvalCommentReaction(Long userId, Long evalCommentId, ReactionType target);
}
