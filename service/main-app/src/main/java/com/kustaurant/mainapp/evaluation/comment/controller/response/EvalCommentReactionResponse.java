package com.kustaurant.mainapp.evaluation.comment.controller.response;

import com.kustaurant.mainapp.common.enums.ReactionType;

public record EvalCommentReactionResponse(
        Long evalCommentId,
        ReactionType reaction,
        Integer likeCount,
        Integer dislikeCount
){}
