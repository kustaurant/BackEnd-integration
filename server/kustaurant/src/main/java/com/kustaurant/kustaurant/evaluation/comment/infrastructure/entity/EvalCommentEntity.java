package com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity;

import com.kustaurant.kustaurant.common.enums.Status;
import com.kustaurant.jpa.common.entity.BaseTimeEntity;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="evaluation_comment")
public class EvalCommentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long restaurantId;
    @Column(nullable = false)
    private Long evaluationId;
    @Column(nullable = false)
    private String body;

    private Integer likeCount;
    private Integer dislikeCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Status status;

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
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }

    public void adjustLikeCount(int num) {
        this.likeCount += num;
    }

    public void adjustDislikeCount(int num) {
        this.dislikeCount += num;
    }
}
