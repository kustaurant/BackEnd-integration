package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import com.kustaurant.kustaurant.post.post.domain.PostDislike;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "post_dislikes_tbl")
public class PostDislikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer postDislikesId;

    @Column(name = "user_id" , nullable = false)
    Long userId;

    @Column(name = "post_id", nullable = false)
    Integer postId;

    LocalDateTime createdAt;

    public PostDislikeEntity() {
    }
    public PostDislikeEntity(Long userId, Integer postId, LocalDateTime createdAt) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public PostDislike toDomain() {
        return PostDislike.builder()
                .id(postDislikesId)
                .userId(this.userId)
                .postId(this.postId)
                .createdAt(this.createdAt)
                .build();
    }

    public static PostDislikeEntity from(PostDislike postDislike) {
        return new PostDislikeEntity(
                postDislike.getUserId(),
                postDislike.getPostId(),
                postDislike.getCreatedAt()
        );
    }

    public static PostDislikeEntity from(PostDislike postDislike, Long userId, Integer postId) {
        PostDislikeEntity postDislikeEntity = new PostDislikeEntity();
        postDislikeEntity.setUserId(userId);
        postDislikeEntity.setPostId(postId);
        postDislikeEntity.setCreatedAt(postDislike.getCreatedAt());
        return postDislikeEntity;
    }

}
