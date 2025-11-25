package com.kustaurant.crawler.aianalysis.adapter.in.messaging.dto;

import java.util.List;

public record AiAnalysisRequest(
        Long restaurantId,
        String url,
        List<String> situations
) {

}
