package com.kustaurant.kustaurant.rating.domain.model;

import java.time.LocalDateTime;

public record EvaluationWithContext(
        long evaluationId,
        double score,
        LocalDateTime evaluatedAt,
        String body,
        boolean existImage,
        int reactionScore,
        double userAvgScore,
        int userEvalCount
) {

}
