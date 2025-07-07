package com.kustaurant.kustaurant.post.comment.infrastructure;

import com.kustaurant.kustaurant.post.comment.domain.PostCommentDislike;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name="post_comment_dislikes_tbl")
public class PostCommentDislikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentDislikeId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "comment_id", nullable = false)
    Integer commentId;

    LocalDateTime createdAt;

    public PostCommentDislikeEntity() {}

    public PostCommentDislikeEntity(
            Integer commentDislikeId,
            Long userId,
            Integer commentId,
            LocalDateTime createdAt
    ) {
        this.commentDislikeId = commentDislikeId;
        this.userId = userId;
        this.commentId = commentId;
        this.createdAt = createdAt;
    }

    public PostCommentDislike toDomain() {
        return PostCommentDislike.builder()
                .id(commentDislikeId)
                .userId(userId)
                .commentId(commentId)
                .createdAt(createdAt)
                .build();
    }

}
