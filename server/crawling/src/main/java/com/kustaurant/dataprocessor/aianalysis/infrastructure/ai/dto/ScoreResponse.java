package com.kustaurant.dataprocessor.aianalysis.infrastructure.ai.dto;

import com.kustaurant.dataprocessor.aianalysis.domain.model.Sentiment;

public record ScoreResponse(
        int score,
        Sentiment sentiment,
        String reason
) {

}
