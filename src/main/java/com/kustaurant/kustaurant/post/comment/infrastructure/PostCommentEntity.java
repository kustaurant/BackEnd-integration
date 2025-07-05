package com.kustaurant.kustaurant.post.comment.infrastructure;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="post_comments_tbl")
public class PostCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentId;
    String commentBody;

    @Column(columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    ContentStatus status;

    @Column(name = "parent_comment_id")
    Integer parentCommentId;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Integer likeCount=0; // 이 likeCount는 좋아요 수에서 싫어요 수를 뺀 순 좋아요 수를 의미

    @Column(name = "post_id", nullable = false)
    Integer postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public PostCommentEntity() {
    }

    public PostCommentEntity(
            String commentBody,
            ContentStatus status,
            LocalDateTime createdAt,
            Integer postId,
            Long userId
    ) {
        this.commentBody = commentBody;
        this.status = status;
        this.createdAt = createdAt;
        this.postId = postId;
        this.userId = userId;
    }

    public static PostCommentEntity from(PostComment comment) {
        PostCommentEntity entity = new PostCommentEntity();
        entity.setCommentId(comment.getId());
        entity.setCommentBody(comment.getCommentBody());
        entity.setStatus(comment.getStatus());
        entity.setLikeCount(comment.getNetLikes());
        entity.setCreatedAt(comment.getCreatedAt());
        entity.setUpdatedAt(comment.getUpdatedAt());
        entity.setPostId(comment.getPostId());
        entity.setUserId(comment.getUserId());
        entity.setParentCommentId(comment.getParentCommentId());

        return entity;
    }

    public PostComment toDomain() {
        return PostComment.builder()
                .id(this.commentId)
                .commentBody(this.commentBody)
                .postId(this.postId)
                .userId(this.userId)
                .netLikes(this.likeCount)
                .status(this.status)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .parentCommentId(this.parentCommentId)
                .build();
    }
}
