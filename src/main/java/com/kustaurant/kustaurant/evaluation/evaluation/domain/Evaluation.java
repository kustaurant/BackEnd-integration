package com.kustaurant.kustaurant.evaluation.evaluation.domain;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class Evaluation {

    private Long id;
    private Double evaluationScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 평가 내용 관련
    private String commentBody;
    private String commentImgUrl;
    private Integer commentLikeCount;
    private List<Long> situationIds;
    // 참조키
    private Long userId;
    private Integer restaurantId;
    // 추가
    private Integer likeCount;
    private Integer dislikeCount;

    public static Evaluation create(Long userId, Integer restaurantId,
            EvaluationDTO dto) {
        return Evaluation.builder()
                .evaluationScore(dto.getEvaluationScore())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .commentBody(dto.getEvaluationComment())
                .commentImgUrl(dto.getEvaluationImgUrl())
                .commentLikeCount(0)
                .situationIds(dto.getEvaluationSituations())
                .userId(userId)
                .restaurantId(restaurantId)
                .likeCount(0)
                .dislikeCount(0)
                .build();
    }

    public void reEvaluate(EvaluationDTO dto) {
        this.evaluationScore = dto.getEvaluationScore();
        this.updatedAt = LocalDateTime.now();
        this.commentBody = dto.getEvaluationComment();
        this.commentImgUrl = dto.getEvaluationImgUrl();
        this.situationIds = dto.getEvaluationSituations();
    }
}
