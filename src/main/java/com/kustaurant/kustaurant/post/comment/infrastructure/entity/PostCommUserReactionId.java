package com.kustaurant.kustaurant.post.comment.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommUserReactionId implements Serializable {
    private Integer postCommentId;
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostCommUserReactionId that)) return false;
        return Objects.equals(postCommentId, that.postCommentId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postCommentId, userId);
    }
}
