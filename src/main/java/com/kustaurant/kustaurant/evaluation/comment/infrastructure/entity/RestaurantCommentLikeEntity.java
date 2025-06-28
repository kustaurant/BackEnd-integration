package com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "restaurant_comment_likes_tbl")
public class RestaurantCommentLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer likeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name="comment_id")
    private RestaurantCommentEntity restaurantComment;

    @ManyToOne
    @JoinColumn(name="evaluation_id")
    private EvaluationEntity evaluation;

    public RestaurantCommentLikeEntity(Long userId, RestaurantCommentEntity restaurantComment) {
        this.restaurantComment = restaurantComment;
        this.userId = userId;
        this.evaluation = null;
        this.createdAt = LocalDateTime.now();
    }

    public RestaurantCommentLikeEntity(Long userId, EvaluationEntity evaluation) {
        this.restaurantComment = null;
        this.userId = userId;
        this.evaluation = evaluation;
        this.createdAt = LocalDateTime.now();
    }

    private LocalDateTime createdAt;
}
