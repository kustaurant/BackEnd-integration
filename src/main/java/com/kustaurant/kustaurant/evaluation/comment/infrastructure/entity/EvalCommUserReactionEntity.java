package com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "evaluation_comment_reaction")
public class EvalCommUserReactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long evalCommentId;
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reaction;


    public EvalCommUserReactionEntity(Long evalCommentId, Long userId, ReactionType reaction) {
        this.evalCommentId = evalCommentId;
        this.userId = userId;
        this.reaction = reaction;
    }

    public void setReaction(ReactionType reaction) {
        this.reaction = reaction;
    }

}
