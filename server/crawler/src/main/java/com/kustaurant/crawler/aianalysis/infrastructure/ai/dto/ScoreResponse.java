package com.kustaurant.crawler.aianalysis.infrastructure.ai.dto;

import com.kustaurant.crawler.aianalysis.domain.model.Sentiment;

public record ScoreResponse(
        int score,
        Sentiment sentiment,
        String reason
) {

}
