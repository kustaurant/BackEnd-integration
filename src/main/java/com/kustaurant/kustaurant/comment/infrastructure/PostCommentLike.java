package com.kustaurant.kustaurant.comment.infrastructure;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PostCommentLike {
    private Integer commentLikeId;
    private Long userId;
    private Integer commentId;
    private LocalDateTime createdAt;


    public PostCommentLike(Integer commentLikeId, Long userId, Integer commentId, LocalDateTime createdAt) {
        this.commentLikeId = commentLikeId;
        this.userId = userId;
        this.commentId = commentId;
        this.createdAt = createdAt;
    }

}
