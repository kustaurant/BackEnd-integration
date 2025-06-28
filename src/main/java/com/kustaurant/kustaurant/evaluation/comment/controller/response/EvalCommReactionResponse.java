package com.kustaurant.kustaurant.evaluation.comment.controller.response;

public record EvalCommReactionResponse(
        Integer commentId,
        Integer commentLikeStatus,   // 1 / 0 / -1
        Integer commentLikeCount,
        Integer commentDislikeCount
){}
