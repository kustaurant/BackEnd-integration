package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation.EvaluationEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
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
public class RestaurantCommentReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "evaluation_id")
    private EvaluationEntity evaluation;
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private RestaurantComment restaurantComment;

    private LocalDateTime createdAt;
    private String status;

    public RestaurantCommentReport(UserEntity UserEntity, RestaurantComment restaurantComment, LocalDateTime createdAt, String status) {
        this.user = UserEntity;
        this.evaluation = null;
        this.restaurantComment = restaurantComment;
        this.createdAt = createdAt;
        this.status = status;
    }
    public RestaurantCommentReport(UserEntity UserEntity, EvaluationEntity evaluation, LocalDateTime createdAt, String status) {
        this.user = UserEntity;
        this.evaluation = evaluation;
        this.restaurantComment = null;
        this.createdAt = createdAt;
        this.status = status;
    }
}
