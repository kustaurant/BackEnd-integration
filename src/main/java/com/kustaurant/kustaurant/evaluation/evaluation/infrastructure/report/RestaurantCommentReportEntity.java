package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.report;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.evaluation.EvaluationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="restaurant_comment_reports_tbl")
@NoArgsConstructor
public class RestaurantCommentReportEntity {
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
    private RestaurantCommentEntity restaurantComment;

    private LocalDateTime createdAt;
    private String status;

    public RestaurantCommentReportEntity(
            Long userId,
            RestaurantCommentEntity restaurantComment,
            LocalDateTime createdAt,
            String status
    ) {
        this.userId = userId;
        this.evaluation = null;
        this.restaurantComment = restaurantComment;
        this.createdAt = createdAt;
        this.status = status;
    }
    public RestaurantCommentReportEntity(
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
