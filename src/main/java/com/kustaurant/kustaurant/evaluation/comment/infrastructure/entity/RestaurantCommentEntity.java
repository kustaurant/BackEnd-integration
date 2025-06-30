package com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.evaluation.EvaluationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.report.RestaurantCommentReportEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="restaurant_comments_tbl")
@NoArgsConstructor
public class RestaurantCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private RestaurantEntity restaurant;

    @ManyToOne
    @JoinColumn(name="evaluation_id")
    private EvaluationEntity evaluation;

    @OneToMany(mappedBy = "restaurantComment")
    private List<RestaurantCommentLikeEntity> restaurantCommentLikeList =new ArrayList<>();

    @OneToMany(mappedBy = "restaurantComment")
    private List<RestaurantCommentDislikeEntity> restaurantCommentDislikeList =new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "restaurantComment")
    private List<RestaurantCommentReportEntity> restaurantCommentReportList = new ArrayList<>();

    private String commentBody;
    private Integer commentLikeCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RestaurantCommentEntity(Long userId, RestaurantEntity restaurant, EvaluationEntity evaluation, String commentBody, String status, LocalDateTime createdAt) {
        this.userId = userId;
        this.restaurant = restaurant;
        this.evaluation = evaluation;
        this.commentBody = commentBody;
        this.status = status;
        this.createdAt = createdAt;
    }
}
