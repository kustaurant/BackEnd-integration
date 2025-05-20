package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="post_comments_likes_tbl_new")
public class PostCommentLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentLikeId;

    public PostCommentLikeEntity(UserEntity user, PostCommentEntity postComment) {
        this.user = user;
        this.postComment = postComment;
        this.createdAt = LocalDateTime.now();
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
                .userId(user.getUserId())
                .commentId(postComment.getCommentId())
                .createdAt(createdAt)
                .build();
    }
}