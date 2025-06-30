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
}
