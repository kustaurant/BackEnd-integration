package com.kustaurant.kustaurant.common.comment;

import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="post_comments_likes_tbl_new")
public class PostCommentLikesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer postCommentLikesId;

    public PostCommentLikesEntity(User user, PostComment postComment) {
        this.user = user;
        this.postComment = postComment;
        this.createdAt = LocalDateTime.now();
    }
    public PostCommentLikesEntity() {

    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name="comment_id")
    PostComment postComment;

    LocalDateTime createdAt;
}