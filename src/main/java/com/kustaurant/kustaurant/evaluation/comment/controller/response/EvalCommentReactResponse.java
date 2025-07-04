package com.kustaurant.kustaurant.evaluation.comment.controller.response;

public record EvalCommentReactResponse(
        Integer commentId,
        Integer commentLikeStatus,   // 1 / 0 / -1
        Integer commentLikeCount,
        Integer commentDislikeCount
){}
