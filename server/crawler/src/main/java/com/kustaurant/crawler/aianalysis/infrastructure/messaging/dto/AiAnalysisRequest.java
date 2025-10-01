package com.kustaurant.crawler.aianalysis.infrastructure.messaging.dto;

import java.util.List;

public record AiAnalysisRequest(
        Long restaurantId,
        String url,
        List<String> situations
) {

}
