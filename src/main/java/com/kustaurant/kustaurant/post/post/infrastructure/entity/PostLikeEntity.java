package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import com.kustaurant.kustaurant.post.post.domain.PostLike;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "post_likes_tbl_new")
public class PostLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer postLikesId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "post_id", nullable = false)
    Integer postId;

    LocalDateTime createdAt;

    public PostLikeEntity() {}

    public PostLikeEntity(Long userId, Integer postId, LocalDateTime createdAt) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public PostLike toDomain() {
        return PostLike.builder()
                .id(postLikesId)
                .userId(this.userId)
                .postId(this.postId)
                .createdAt(this.createdAt)
                .build();
    }

    public static PostLikeEntity from(PostLike postLike) {
        return new PostLikeEntity(
                postLike.getUserId(),
                postLike.getPostId(),
                postLike.getCreatedAt()
        );
    }

}
