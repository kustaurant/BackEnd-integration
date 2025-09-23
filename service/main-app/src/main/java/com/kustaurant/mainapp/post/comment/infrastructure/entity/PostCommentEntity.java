package com.kustaurant.mainapp.post.comment.infrastructure.entity;

import com.kustaurant.jpa.common.entity.BaseTimeEntity;
import com.kustaurant.mainapp.post.comment.domain.PostComment;
import com.kustaurant.mainapp.post.comment.domain.PostCommentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@SQLDelete(sql = "update post_comment set status = 'DELETED' where post_comment_id = ?")
@SQLRestriction("status <> 'DELETED'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="post_comment")
public class PostCommentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postCommentId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String commentBody;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private PostCommentStatus status;


    @Builder
    public PostCommentEntity(
            Long postCommentId,
            String commentBody,
            PostCommentStatus status,
            Long postId,
            Long parentCommentId,
            Long userId
    ) {
        this.postCommentId = postCommentId;
        this.commentBody = commentBody;
        this.status = status;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.userId = userId;
    }

    public static PostCommentEntity from(PostComment comment) {
        return PostCommentEntity.builder()
                .commentBody(comment.getBody())
                .status(comment.getStatus())
                .postId(comment.getPostId())
                .userId(comment.getWriterId())
                .parentCommentId(comment.getParentCommentId())
                .build();
    }

    public PostComment toModel() {
        return PostComment.builder()
                .id(this.postCommentId)
                .body(this.commentBody)
                .postId(this.postId)
                .writerId(this.userId)
                .status(this.status)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .parentCommentId(this.parentCommentId)
                .build();
    }
}
