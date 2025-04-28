package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="restaurant_comments_tbl")
@NoArgsConstructor
public class RestaurantComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private RestaurantEntity restaurant;

    @ManyToOne
    @JoinColumn(name="evaluation_id")
    private EvaluationEntity evaluation;

    @OneToMany(mappedBy = "restaurantComment")
    private List<RestaurantCommentLike> restaurantCommentLikeList =new ArrayList<>();

    @OneToMany(mappedBy = "restaurantComment")
    private List<RestaurantCommentDislike> restaurantCommentDislikeList =new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "restaurantComment")
    private List<RestaurantCommentReport> restaurantCommentReportList = new ArrayList<>();

    private String commentBody;
    private Integer commentLikeCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RestaurantComment(UserEntity UserEntity, RestaurantEntity restaurant, EvaluationEntity evaluation, String commentBody, String status, LocalDateTime createdAt) {
        this.user = UserEntity;
        this.restaurant = restaurant;
        this.evaluation = evaluation;
        this.commentBody = commentBody;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String calculateTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = this.getCreatedAt();

        // 연 차이 계산
        Long yearsDifference = ChronoUnit.YEARS.between(past, now);
        if (yearsDifference > 0) return yearsDifference.toString() + "년 전";

        // 월 차이 계산
        Long monthsDifference = ChronoUnit.MONTHS.between(past, now);
        if (monthsDifference > 0) return monthsDifference.toString() + "달 전";

        // 일 차이 계산
        Long daysDifference = ChronoUnit.DAYS.between(past, now);
        if (daysDifference > 0) return daysDifference.toString() + "일 전";

        // 시간 차이 계산
        Long hoursDifference = ChronoUnit.HOURS.between(past, now);
        if (hoursDifference > 0) return hoursDifference.toString() + "시간 전";

        // 분 차이 계산
        Long minutesDifference = ChronoUnit.MINUTES.between(past, now);
        if (minutesDifference > 0) return minutesDifference.toString() + "분 전";

        // 초 차이 계산
        Long secondsDifference = ChronoUnit.SECONDS.between(past, now);
        return secondsDifference.toString() + "초 전";
    }
}
