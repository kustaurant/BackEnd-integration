package com.kustaurant.kustaurant.post.comment.infrastructure.entity;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(PostCommUserReactionId.class)
@Table(name="post_comm_user_reaction")
public class PostCommUserReactionEntity {
    @Id
    @Column(name = "post_comm_id")
    private Integer postCommentId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reaction;

    @Column(name = "reacted_at", nullable = false)
    private LocalDateTime reactedAt;

    public static PostCommUserReactionEntity of(
            Integer postCommentId, Long userId, ReactionType reaction
    ) {
        PostCommUserReactionEntity entity = new PostCommUserReactionEntity();
        entity.postCommentId   = postCommentId;
        entity.userId   = userId;
        entity.reaction = reaction;
        return entity;
    }

    public void changeTo(ReactionType newReaction) {
        this.reaction = newReaction;
    }
}
