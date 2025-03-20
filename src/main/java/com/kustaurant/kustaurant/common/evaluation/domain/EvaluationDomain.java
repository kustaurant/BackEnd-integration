package com.kustaurant.kustaurant.common.evaluation.domain;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
    private User user;
    private RestaurantDomain restaurant;
}
