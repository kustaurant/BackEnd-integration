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
@Table(name="post_comments_dislikes_tbl_new")
public class PostCommentDislikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentDislikeId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @ManyToOne
    @JoinColumn(name="comment_id")
    PostCommentEntity postComment;

    LocalDateTime createdAt;

    public PostCommentDislikeEntity() {}

    public PostCommentDislikeEntity(
            Integer commentDislikeId,
            Long userId,
            PostCommentEntity postComment,
            LocalDateTime createdAt
    ) {
        this.commentDislikeId = commentDislikeId;
        this.userId = userId;
        this.postComment = postComment;
        this.createdAt = createdAt;
    }

}
