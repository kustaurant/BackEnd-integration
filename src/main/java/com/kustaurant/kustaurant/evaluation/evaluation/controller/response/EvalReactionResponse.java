package com.kustaurant.kustaurant.evaluation.evaluation.controller.response;

import com.kustaurant.kustaurant.common.enums.ReactionType;

public record EvalReactionResponse(
        Long evaluationId,
        ReactionType reaction,
        Integer likeCount,
        Integer dislikeCount
){}
