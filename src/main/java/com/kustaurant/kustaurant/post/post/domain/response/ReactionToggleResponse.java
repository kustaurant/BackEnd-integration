package com.kustaurant.kustaurant.post.post.domain.response;

import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
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
