package com.kustaurant.restauranttier.tab3_tier.entity;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
    private User user;
    @ManyToOne
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private RestaurantComment restaurantComment;

    private LocalDateTime createdAt;
    private String status;

    public RestaurantCommentReport(User user, RestaurantComment restaurantComment, LocalDateTime createdAt, String status) {
        this.user = user;
        this.evaluation = null;
        this.restaurantComment = restaurantComment;
        this.createdAt = createdAt;
        this.status = status;
    }
    public RestaurantCommentReport(User user, Evaluation evaluation, LocalDateTime createdAt, String status) {
        this.user = user;
        this.evaluation = evaluation;
        this.restaurantComment = null;
        this.createdAt = createdAt;
        this.status = status;
    }
}
