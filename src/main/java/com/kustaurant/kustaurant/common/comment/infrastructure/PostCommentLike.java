package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PostCommentLike {
    private Integer commentLikeId;
    private Integer userId;
    private Integer commentId;
    private LocalDateTime createdAt;


    public PostCommentLike(Integer commentLikeId, Integer userId, Integer commentId, LocalDateTime createdAt) {
        this.commentLikeId = commentLikeId;
        this.userId = userId;
        this.commentId = commentId;
        this.createdAt = createdAt;
    }

}
