package com.kustaurant.kustaurant.evaluation.evaluation.domain;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
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
    private List<Long> situationIds;
    // 참조키
    private Long userId;
    private Long restaurantId;
    // 추가
    private Integer likeCount;
    private Integer dislikeCount;

    public static Evaluation create(Long userId, Long restaurantId, EvaluationDTO dto) {
        return Evaluation.builder()
                .evaluationScore(dto.getEvaluationScore())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .commentBody(dto.getEvaluationComment())
                .commentImgUrl(dto.getEvaluationImgUrl())
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

    public void adjustLikeCount(int num) {
        this.likeCount += num;
    }
    public void adjustDislikeCount(int num) {
        this.dislikeCount += num;
    }
}
