package com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "restaurant_comment_likes_tbl")
public class EvalCommentLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer likeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name="comment_id")
    private Long evalCommentId;

    public EvalCommentLikeEntity(Long userId, Long evalCommentId) {
        this.evalCommentId = evalCommentId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    public EvalCommentLikeEntity(Long userId, EvaluationEntity evaluation) {
        this.restaurantComment = null;
        this.userId = userId;
        this.evaluation = evaluation;
        this.createdAt = LocalDateTime.now();
    }

    private LocalDateTime createdAt;
}
