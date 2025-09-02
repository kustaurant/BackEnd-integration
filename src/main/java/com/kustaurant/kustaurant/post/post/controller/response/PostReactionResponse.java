package com.kustaurant.kustaurant.post.post.controller.response;

import com.kustaurant.kustaurant.common.enums.ReactionType;


public record PostReactionResponse (
        ReactionType reactionType,
        int likeCount,
        int dislikeCount,
        int netLikes
){}
