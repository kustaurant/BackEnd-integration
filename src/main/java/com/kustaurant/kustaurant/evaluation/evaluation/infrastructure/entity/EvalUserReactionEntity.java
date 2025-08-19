package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="evaluation_reaction")
public class EvalUserReactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private long evaluationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reaction;

    public EvalUserReactionEntity(long userId, long evaluationId, ReactionType reaction) {
        this.userId = userId;
        this.evaluationId = evaluationId;
        this.reaction = reaction;
    }

    public void updateReaction(ReactionType reaction) {
        this.reaction = reaction;
    }
}
