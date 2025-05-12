package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.user.domain.User;

import java.time.LocalDateTime;

public class PostCommentLike {
    private final Integer userId;
    private final Integer commentId;
    private final LocalDateTime createdAt;

    public static PostCommentLike create(User user, PostComment comment) {
        return new PostCommentLike(user.getId(), comment.getCommentId(), LocalDateTime.now());
    }

    public PostCommentLike(Integer userId, Integer commentId, LocalDateTime createdAt) {
        this.userId = userId;
        this.commentId = commentId;
        this.createdAt = createdAt;
    }

    public boolean isBy(User user) {
        return this.userId.equals(user.getId());
    }
}
