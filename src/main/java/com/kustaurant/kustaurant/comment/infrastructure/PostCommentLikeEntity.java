package com.kustaurant.kustaurant.comment.infrastructure;

import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
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
    public PostCommentLikeEntity(Integer commentLikeId, UserEntity user, PostCommentEntity postComment, LocalDateTime createdAt) {
        this.commentLikeId = commentLikeId;
        this.user = user;
        this.postComment = postComment;
        this.createdAt = createdAt;
    }
    public PostCommentLikeEntity() {
    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    @ManyToOne
    @JoinColumn(name="comment_id")
    PostCommentEntity postComment;

    LocalDateTime createdAt;

    public PostCommentLike toDomain(){
        return PostCommentLike.builder()
                .commentLikeId(commentLikeId)
                .userId(user.getId())
                .commentId(postComment.getCommentId())
                .createdAt(createdAt)
                .build();
    }

}