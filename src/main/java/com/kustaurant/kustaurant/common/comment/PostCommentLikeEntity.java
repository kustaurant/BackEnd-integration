package com.kustaurant.kustaurant.common.comment;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
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
    Integer likeId;

    public PostCommentLikeEntity(User user, PostComment postComment) {
        this.user = user;
        this.postComment = postComment;
        this.createdAt = LocalDateTime.now();
    }
    public PostCommentLikeEntity() {

    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name="comment_id")
    PostComment postComment;

    LocalDateTime createdAt;
}