package com.kustaurant.kustaurant.post.post.controller.response;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PostReactionResponse {
    private ReactionType reactionType;
    private int likeCount;
    private int dislikeCount;
    private int netLikes;

    public PostReactionResponse(ReactionType reactionType, int netLikes, int likeCount, int dislikeCount) {
        this.reactionType = reactionType;
        this.netLikes = netLikes;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
    }
}
