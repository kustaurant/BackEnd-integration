package com.kustaurant.kustaurant.comment.infrastructure;

import com.kustaurant.kustaurant.comment.domain.PostComment;
import com.kustaurant.kustaurant.user.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Builder
@Getter
public class PostCommentDislike {
    private Long userId;
    private Integer commentId;
    private LocalDateTime createdAt;

    public static PostCommentDislike create(User user, PostComment comment) {
        return new PostCommentDislike(user.getId(), comment.getCommentId(), LocalDateTime.now());
    }

    public PostCommentDislike(Long userId, Integer commentId, LocalDateTime createdAt) {
        this.userId = userId;
        this.commentId = commentId;
        this.createdAt = createdAt;
    }

    public boolean isBy(User user) {
        return this.userId.equals(user.getId());
    }
}
