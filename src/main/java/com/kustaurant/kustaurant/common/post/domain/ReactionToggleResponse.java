package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReactionToggleResponse {
    private ReactionStatus status;
    private int likeCount;
    private int dislikeCount;
    private int netLikes;

    public ReactionToggleResponse(ReactionStatus status, int netLikes, int likeCount, int dislikeCount) {
        this.status = status;
        this.netLikes = netLikes;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
    }
}
