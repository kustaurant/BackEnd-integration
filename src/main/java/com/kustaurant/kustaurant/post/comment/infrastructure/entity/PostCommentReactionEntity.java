package com.kustaurant.kustaurant.post.comment.infrastructure.entity;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(PostCommUserReactionId.class)
@Table(name="post_comment_reaction")
public class PostCommentReactionEntity {
    @Id
    @Column(name = "post_comment_id")
    private Integer postCommentId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reaction;

    @CreationTimestamp
    @Column(name = "reacted_at", nullable = false)
    private LocalDateTime reactedAt;

    public static PostCommentReactionEntity of(
            Integer postCommentId, Long userId, ReactionType reaction
    ) {
        PostCommentReactionEntity entity = new PostCommentReactionEntity();
        entity.postCommentId   = postCommentId;
        entity.userId   = userId;
        entity.reaction = reaction;
        return entity;
    }

    public void changeTo(ReactionType newReaction) {
        this.reaction = newReaction;
    }
}
