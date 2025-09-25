package com.kustaurant.crawler.aianalysis.infrastructure.messaging.dto;

import java.util.List;

public record AiAnalysisRequest(
        String naverId,
        String url,
        List<String> situations
) {

}
