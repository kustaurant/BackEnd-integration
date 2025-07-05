package com.kustaurant.kustaurant.post.comment.domain;

import com.kustaurant.kustaurant.user.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PostCommentDislike {
    private Integer id;
    private Long userId;
    private Integer commentId;
    private LocalDateTime createdAt;

    public static PostCommentDislike create(User user, PostComment comment) {
        return new PostCommentDislike(null, user.getId(), comment.getId(), LocalDateTime.now());
    }

    public PostCommentDislike(Integer id, Long userId, Integer commentId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.commentId = commentId;
        this.createdAt = createdAt;
    }

    public boolean isBy(User user) {
        return this.userId.equals(user.getId());
    }
}
