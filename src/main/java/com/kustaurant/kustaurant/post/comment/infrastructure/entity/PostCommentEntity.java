package com.kustaurant.kustaurant.post.comment.infrastructure.entity;

import com.kustaurant.kustaurant.common.infrastructure.BaseTimeEntity;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentStatus;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@SQLDelete(sql = "update post_comments_tbl set status = 'DELETED' where comment_id = ?")
@SQLRestriction("status = 'ACTIVE'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="post_comments_tbl")
public class PostCommentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentId;
    String commentBody;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    PostCommentStatus status;

    @Column(name = "parent_comment_id")
    Integer parentCommentId;

    @Column(name = "post_id", nullable = false)
    Integer postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    public PostCommentEntity(
            String commentBody,
            PostCommentStatus status,
            Integer postId,
            Integer parentCommentId,
            Long userId
    ) {
        this.commentBody = commentBody;
        this.status = status;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.userId = userId;
    }

    public static PostCommentEntity from(PostComment comment) {
        return PostCommentEntity.builder()
                .commentBody(comment.getCommentBody())
                .status(comment.getStatus())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .parentCommentId(comment.getParentCommentId())
                .build();
    }

    public PostComment toModel() {
        return PostComment.builder()
                .id(this.commentId)
                .commentBody(this.commentBody)
                .postId(this.postId)
                .userId(this.userId)
                .status(this.status)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .parentCommentId(this.parentCommentId)
                .build();
    }
}
