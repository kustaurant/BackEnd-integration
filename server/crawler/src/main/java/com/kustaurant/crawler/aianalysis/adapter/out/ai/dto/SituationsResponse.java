package com.kustaurant.crawler.aianalysis.adapter.out.ai.dto;

import java.util.List;

public record SituationsResponse(
        List<String> situations,
        String reason
) {
}
