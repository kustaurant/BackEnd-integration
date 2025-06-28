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

    @ManyToOne
    @JoinColumn(name = "post_id")
    PostEntity post;

    LocalDateTime createdAt;

    public PostLikeEntity() {}

    public PostLikeEntity(Long userId, PostEntity post, LocalDateTime createdAt) {
        this.userId = userId;
        this.post = post;
        this.createdAt = createdAt;
    }

    public PostLike toDomain() {
        return new PostLike(
                this.userId,
                this.post.getPostId(),
                this.createdAt
        );
    }

    public static PostLikeEntity from(PostLike postLike) {
        PostEntity postEntity = new PostEntity();
        postEntity.setPostId(postLike.getPostId());
        return new PostLikeEntity(
                postLike.getUserId(),
                postEntity,
                postLike.getCreatedAt()
        );
    }

}
