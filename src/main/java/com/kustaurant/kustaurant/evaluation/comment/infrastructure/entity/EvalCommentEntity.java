package com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity;

import com.kustaurant.kustaurant.common.enums.Status;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="eval_comment")
public class EvalCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Integer restaurantId;
    private Long evaluationId;
    private String body;

    private Integer likeCount;
    private Integer dislikeCount;

    @Enumerated(EnumType.STRING)           // ← enum 으로 매핑
    @Column(nullable = false, length = 8)
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EvalCommentEntity from(EvalComment evalComment) {
        EvalCommentEntity evalCommentEntity = new EvalCommentEntity();
        evalCommentEntity.id = evalComment.getId();
        evalCommentEntity.userId = evalComment.getUserId();
        evalCommentEntity.restaurantId = evalComment.getRestaurantId();
        evalCommentEntity.evaluationId = evalComment.getEvaluationId();
        evalCommentEntity.body = evalComment.getBody();
        evalCommentEntity.likeCount = evalComment.getLikeCount();
        evalCommentEntity.dislikeCount = evalComment.getDislikeCount();
        evalCommentEntity.status = evalComment.getStatus();
        evalCommentEntity.createdAt = evalComment.getCreatedAt();
        evalCommentEntity.updatedAt = evalComment.getUpdatedAt();
        return evalCommentEntity;
    }

    public EvalComment toModel() {
        return EvalComment.builder()
                .id(id)
                .userId(userId)
                .restaurantId(restaurantId)
                .evaluationId(evaluationId)
                .body(body)
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public void adjustLikeCount(int num) {
        this.likeCount += num;
    }

    public void adjustDislikeCount(int num) {
        this.dislikeCount += num;
    }
}
