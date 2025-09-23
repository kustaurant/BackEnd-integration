package com.kustaurant.mainapp.post.comment.infrastructure.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentReactionJpaId implements Serializable {
    private Long postCommentId;
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostCommentReactionJpaId that)) return false;
        return Objects.equals(postCommentId, that.postCommentId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postCommentId, userId);
    }
}
