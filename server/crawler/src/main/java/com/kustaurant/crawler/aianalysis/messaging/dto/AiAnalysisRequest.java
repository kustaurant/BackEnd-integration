package com.kustaurant.crawler.aianalysis.messaging.dto;

import java.util.List;

public record AiAnalysisRequest(
        Long restaurantId,
        String url,
        List<String> situations
) {

}
