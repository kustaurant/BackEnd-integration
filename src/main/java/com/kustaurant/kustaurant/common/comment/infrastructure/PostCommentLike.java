package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.user.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class PostCommentLike {
    private final Integer commentLikeId;
    private final Integer userId;
    private final Integer commentId;
    private final LocalDateTime createdAt;


    public PostCommentLike(Integer commentLikeId, Integer userId, Integer commentId, LocalDateTime createdAt) {
        this.commentLikeId = commentLikeId;
        this.userId = userId;
        this.commentId = commentId;
        this.createdAt = createdAt;
    }

}
