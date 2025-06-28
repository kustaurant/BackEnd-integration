package com.kustaurant.kustaurant.post.comment.infrastructure;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name="post_comments_likes_tbl_new")
public class PostCommentLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentLikeId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @ManyToOne
    @JoinColumn(name="comment_id")
    PostCommentEntity postComment;

    LocalDateTime createdAt;

    public PostCommentLikeEntity() {
    }

    public PostCommentLikeEntity(
            Integer commentLikeId,
            Long userId,
            PostCommentEntity postComment,
            LocalDateTime createdAt
    ) {
        this.commentLikeId = commentLikeId;
        this.userId = userId;
        this.postComment = postComment;
        this.createdAt = createdAt;
    }

    public PostCommentLike toDomain(){
        return PostCommentLike.builder()
                .commentLikeId(commentLikeId)
                .userId(userId)
                .commentId(postComment.getCommentId())
                .createdAt(createdAt)
                .build();
    }

}