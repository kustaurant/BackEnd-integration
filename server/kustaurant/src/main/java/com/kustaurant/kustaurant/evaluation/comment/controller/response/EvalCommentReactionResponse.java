package com.kustaurant.kustaurant.evaluation.comment.controller.response;

import com.kustaurant.kustaurant.common.enums.ReactionType;

public record EvalCommentReactionResponse(
        Long evalCommentId,
        ReactionType reaction,
        Integer likeCount,
        Integer dislikeCount
){}
