package com.kustaurant.kustaurant.admin.report;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommentEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="report")
@NoArgsConstructor
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "evaluation_id")
    private EvaluationEntity evaluation;
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private EvalCommentEntity restaurantComment;

    private LocalDateTime createdAt;
    private String status;

    public ReportEntity(
            Long userId,
            EvalCommentEntity restaurantComment,
            LocalDateTime createdAt,
            String status
    ) {
        this.userId = userId;
        this.evaluation = null;
        this.restaurantComment = restaurantComment;
        this.createdAt = createdAt;
        this.status = status;
    }
    public ReportEntity(
            Long userId,
            EvaluationEntity evaluation,
            LocalDateTime createdAt,
            String status
    ) {
        this.userId = userId;
        this.evaluation = evaluation;
        this.restaurantComment = null;
        this.createdAt = createdAt;
        this.status = status;
    }
}
