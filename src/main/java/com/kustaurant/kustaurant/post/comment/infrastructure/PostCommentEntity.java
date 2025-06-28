package com.kustaurant.kustaurant.post.comment.infrastructure;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name="parent_comment_id")
    PostCommentEntity parentComment;

    @OneToMany(mappedBy = "parentComment")
    List<PostCommentEntity> repliesList = new ArrayList<>();
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Integer likeCount=0; // 이 likeCount는 좋아요 수에서 싫어요 수를 뺀 순 좋아요 수를 의미

    @ManyToOne
    @JoinColumn(name="post_id")
    PostEntity post;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "postComment")
    List<PostCommentLikeEntity> postCommentLikesEntities = new ArrayList<>();
    @OneToMany(mappedBy = "postComment")
    List<PostCommentDislikeEntity> postCommentDislikesEntities = new ArrayList<>();

    public PostCommentEntity() {
    }

    public PostCommentEntity(
            String commentBody,
            ContentStatus status,
            LocalDateTime createdAt,
            PostEntity post,
            Long userId
    ) {
        this.commentBody = commentBody;
        this.status = status;
        this.createdAt = createdAt;
        this.post = post;
        this.userId = userId;
    }

    public static PostCommentEntity from(PostComment comment) {
        PostCommentEntity entity = new PostCommentEntity();
        entity.setCommentId(comment.getCommentId());
        entity.setCommentBody(comment.getCommentBody());
        entity.setStatus(comment.getStatus());
        entity.setLikeCount(comment.getNetLikes());
        entity.setCreatedAt(comment.getCreatedAt());
        entity.setUpdatedAt(comment.getUpdatedAt());

        // postId 처리
        if (comment.getPostId() != null) {
            PostEntity postEntity = new PostEntity();
            postEntity.setPostId(comment.getPostId());
            entity.setPost(postEntity);
        }

        // parentCommentId 처리
        if (comment.getParentComment() != null) {
            PostCommentEntity parent = new PostCommentEntity();
            parent.setCommentId(comment.getParentComment().getCommentId());
            entity.setParentComment(parent);
        }

        return entity;
    }

    public PostComment toDomain(boolean includeParent, boolean includeReplies) {
        PostComment.PostCommentBuilder builder = PostComment.builder()
                .commentId(this.commentId)
                .commentBody(this.commentBody)
                .postId(this.post.getPostId())
                .userId(this.userId)
                .netLikes(this.likeCount)
                .status(this.status)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .likeCount(this.postCommentLikesEntities.size())
                .dislikeCount(this.postCommentDislikesEntities.size());

        if (includeParent && this.parentComment != null) {
            builder.parentComment(this.parentComment.toDomain(false, false)); // 깊이 제한
        }

        if (includeReplies && this.repliesList != null && !this.repliesList.isEmpty()) {
            List<PostComment> replies = new ArrayList<>();
            for (PostCommentEntity replyEntity : this.repliesList) {
                if (!replyEntity.getCommentId().equals(this.commentId)) {
                    replies.add(replyEntity.toDomain(false, true));
                }
            }
            builder.replies(replies);
        } else {
            builder.replies(new ArrayList<>());
        }


        return builder.build();
    }

    public PostComment toDomain() {
        return toDomain(false, true);
    }
}
