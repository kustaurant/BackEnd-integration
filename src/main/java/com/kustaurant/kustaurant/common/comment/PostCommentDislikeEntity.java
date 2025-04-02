package com.kustaurant.kustaurant.common.comment;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="post_comments_dislikes_tbl_new")
public class PostCommentDislikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentDislikeId;

    public PostCommentDislikeEntity(UserEntity user, PostComment postComment) {
        this.user = user;
        this.postComment = postComment;
        this.createdAt = LocalDateTime.now();
    }
    public PostCommentDislikeEntity() {

    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    @ManyToOne
    @JoinColumn(name="comment_id")
    PostComment postComment;

    LocalDateTime createdAt;
}
