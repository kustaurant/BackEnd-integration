package com.kustaurant.kustaurant.evaluation.evaluation.service.port.dto;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDTO;
import java.util.List;
import lombok.Builder;

@Builder
public record EvaluationSaveCondition(
        Long userId,
        Integer restaurantId,
        Double evaluationScore,
        String imgUrl,
        String comment
) {

    public static EvaluationSaveCondition from(Long userId, Integer restaurantId, EvaluationDTO dto) {
        return EvaluationSaveCondition.builder()
                .userId(userId)
                .restaurantId(restaurantId)
                .evaluationScore(dto.getEvaluationScore())
                .imgUrl(dto.getEvaluationImgUrl())
                .comment(dto.getEvaluationComment())
                .build();
    }
}
