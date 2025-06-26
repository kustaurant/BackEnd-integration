package com.kustaurant.kustaurant.evaluation.domain;

import com.kustaurant.kustaurant.evaluation.infrastructure.EvaluationEntity;
import com.kustaurant.kustaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.user.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class EvaluationDomain {
    private Integer evaluationId;
    private Double evaluationScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 평가 내용 관련
    private String commentBody;
    private String commentImgUrl;
    private Integer commentLikeCount = 0;

    // 참조키
    private Long userId;
    private Restaurant restaurant;

    // 추가
    private Integer likeCount;
    private Integer dislikeCount;
    private List<EvaluationSituation> evaluationSituationList = new ArrayList<>();


    public static EvaluationDomain from(EvaluationEntity entity) {
        if (entity == null) return null;

        return EvaluationDomain.builder()
                .evaluationId(entity.getEvaluationId())
                .evaluationScore(entity.getEvaluationScore())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .commentBody(entity.getCommentBody())
                .commentImgUrl(entity.getCommentImgUrl())
                .commentLikeCount(entity.getCommentLikeCount() != null ? entity.getCommentLikeCount() : 0)
                .userId(entity.getUserId())
                .restaurant(Restaurant.from(entity.getRestaurant()))
                .likeCount(entity.getRestaurantCommentLikeList() != null ? entity.getRestaurantCommentLikeList().size() : 0)
                .dislikeCount(entity.getRestaurantCommentDislikeList() != null ? entity.getRestaurantCommentDislikeList().size() : 0)
                .evaluationSituationList(
                        entity.getEvaluationSituationEntityList() != null ?
                                entity.getEvaluationSituationEntityList().stream()
                                        .map(e -> EvaluationSituation.builder()
                                                .situation(Situation.builder()
                                                        .situationId(e.getSituation().getSituationId())
                                                        .situationName(e.getSituation().getSituationName())
                                                        .build())
                                                .build()
                                        )
                                        .toList()
                                : new ArrayList<>()
                )
                .build();
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
