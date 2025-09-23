package com.kustaurant.mainapp.evaluation.evaluation.controller.response;

import com.kustaurant.mainapp.common.enums.ReactionType;

public record EvalReactionResponse(
        Long evaluationId,
        ReactionType reaction,
        Integer likeCount,
        Integer dislikeCount
){}
