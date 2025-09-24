package com.kustaurant.kustaurant.post.post.controller.response;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;


public record PostReactionResponse (
        @Schema(description = "유저의 반응 상태(LIKE,DISLIKE,null(아무 상호작용 안함)", example = "LIKE")
        ReactionType reactionType,
        int likeCount,
        int dislikeCount,
        int netLikes
){}
