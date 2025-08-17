package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(PostUserReactionId.class)
@Table(name="post_user_reaction")
public class PostReactionEntity {
    @Id
    @Column(name = "post_id")
    private Integer postId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reaction;

    @Column(name = "reacted_at", nullable = false)
    private LocalDateTime reactedAt;

    public static PostReactionEntity of(
            Integer postId, Long userId, ReactionType reaction
    ) {
        PostReactionEntity entity = new PostReactionEntity();
        entity.postId   = postId;
        entity.userId   = userId;
        entity.reaction = reaction;
        return entity;
    }

    public void changeTo(ReactionType newReaction) {
        this.reaction = newReaction;
    }
}
