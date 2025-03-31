package com.kustaurant.kustaurant.common.comment;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="post_comments_dislikes_tbl_new")
public class PostCommentDislikesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer postCommentDislikesId;

    public PostCommentDislikesEntity(User user, PostComment postComment) {
        this.user = user;
        this.postComment = postComment;
        this.createdAt = LocalDateTime.now();
    }
    public PostCommentDislikesEntity() {

    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name="comment_id")
    PostComment postComment;

    LocalDateTime createdAt;
}
