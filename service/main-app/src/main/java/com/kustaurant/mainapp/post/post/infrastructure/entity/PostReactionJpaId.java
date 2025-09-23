package com.kustaurant.mainapp.post.post.infrastructure.entity;

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
public class PostReactionJpaId implements Serializable {
    private Long postId;
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostReactionJpaId that)) return false;
        return Objects.equals(postId, that.postId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }
}
