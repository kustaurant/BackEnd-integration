package com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="eval_comment")
public class EvalCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name="eval_comment_id")
    private Integer evalCommentId;

    private String commentBody;
    private Integer likeCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EvalCommentEntity(Long userId, Integer restaurantId, Integer evaluationId, String commentBody, String status, LocalDateTime createdAt) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.evaluationId = evaluationId;
        this.commentBody = commentBody;
        this.status = status;
        this.createdAt = createdAt;
    }
}
