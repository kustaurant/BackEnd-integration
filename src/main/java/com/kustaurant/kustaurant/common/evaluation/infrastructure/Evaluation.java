package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
@Getter
@Entity
@Setter
// 한 사용자가 한 식당을 중복 평가 할 수 없음
@Table(name="evaluations_tbl")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer evaluationId;

    private Double evaluationScore;

    private String status="ACTIVE";

    public Evaluation(RestaurantEntity restaurant, UserEntity UserEntity, Double evaluationScore) {
        this.restaurant = restaurant;
        this.user = UserEntity;
        this.evaluationScore = evaluationScore;
        this.createdAt = LocalDateTime.now();
    }

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 평가 내용 관련
    private String commentBody;
    private String commentImgUrl;
    private Integer commentLikeCount = 0;
    @OneToMany(mappedBy = "evaluation")
    private List<RestaurantCommentLike> restaurantCommentLikeList = new ArrayList<>();
    @OneToMany(mappedBy = "evaluation")
    private List<RestaurantCommentDislike> restaurantCommentDislikeList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "evaluation")
    private List<EvaluationItemScore> evaluationItemScoreList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "evaluation")
    private List<RestaurantCommentReport> restaurantCommentReportList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "evaluation")
    private List<RestaurantComment> restaurantCommentList = new ArrayList<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private RestaurantEntity restaurant;

    public Evaluation() {

    }

    public Evaluation(Double evaluationScore, String status, LocalDateTime createdAt, String commentBody, String commentImgUrl, UserEntity UserEntity, RestaurantEntity restaurant) {
        this.evaluationScore = evaluationScore;
        this.status = status;
        this.createdAt = createdAt;
        this.commentBody = commentBody;
        this.commentImgUrl = commentImgUrl;
        this.user = UserEntity;
        this.restaurant = restaurant;
    }

    // 평가에서 선택한 situation들의 id 리스트를 반환합니다. 이전에 선택한게 없을경우 null을 반환합니다.
    public List<Integer> getSituationIdList() {
        if (this.evaluationItemScoreList == null || this.evaluationItemScoreList.isEmpty()) {
            return null;
        }

        return this.evaluationItemScoreList.stream()
                .map(evaluationItemScore -> evaluationItemScore.getSituation().getSituationId())
                .toList();
    }

    public String getStarImgUrl() {
        try {
            String[] scoreSplit = evaluationScore.toString().split("\\.");
            if (scoreSplit.length > 1) {
                return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/star/star" + scoreSplit[0] + scoreSplit[1] + ".svg";
            } else {
                return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/star/star" + scoreSplit[0] + "0.svg";
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String calculateTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = this.getCreatedAt();
        if (this.getUpdatedAt() != null) {
            past = this.getUpdatedAt();
        }

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
