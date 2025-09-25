package com.kustaurant.crawler.aianalysis.infrastructure.ai.dto;

import java.util.List;

public record SituationsResponse(
        List<String> situations,
        String reason
) {
}
