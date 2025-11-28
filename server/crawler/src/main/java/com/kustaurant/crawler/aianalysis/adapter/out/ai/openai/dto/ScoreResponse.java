package com.kustaurant.crawler.aianalysis.adapter.out.ai.openai.dto;

import com.kustaurant.crawler.aianalysis.domain.model.Sentiment;

public record ScoreResponse(
        int score,
        Sentiment sentiment,
        String reason
) {

}
