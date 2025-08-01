package com.kustaurant.kustaurant.post.comment.infrastructure.entity;

import com.kustaurant.kustaurant.post.comment.domain.PostCommentLike;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name="post_comment_likes_tbl")
public class PostCommentLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentLikeId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "comment_id", nullable = false)
    Integer commentId;

    LocalDateTime createdAt;

    public PostCommentLikeEntity() {
    }

    public PostCommentLikeEntity(
            Integer commentLikeId,
            Long userId,
            Integer commentId,
            LocalDateTime createdAt
    ) {
        this.commentLikeId = commentLikeId;
        this.userId = userId;
        this.commentId = commentId;
        this.createdAt = createdAt;
    }

    public PostCommentLike toDomain(){
        return PostCommentLike.builder()
                .commentLikeId(commentLikeId)
                .userId(userId)
                .commentId(commentId)
                .createdAt(createdAt)
                .build();
    }

}