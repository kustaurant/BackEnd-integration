package com.kustaurant.crawler.aianalysis.adapter.out.ai.openai.dto;

import java.util.List;

public record SituationsResponse(
        List<String> situations,
        String reason
) {
}
