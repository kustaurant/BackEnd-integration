package com.kustaurant.kustaurant.evaluation.evaluation.domain;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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
    private Integer commentLikeCount;
    // 참조키
    private Long userId;
    private Restaurant restaurant;
    // 추가
    private Integer likeCount;
    private Integer dislikeCount;
    private List<EvaluationSituation> evaluationSituationList;

    public static EvaluationDomain from(EvaluationEntity entity) {
        if (entity == null) return null;

        return EvaluationDomain.builder()
                .evaluationId(entity.getId())
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
}
