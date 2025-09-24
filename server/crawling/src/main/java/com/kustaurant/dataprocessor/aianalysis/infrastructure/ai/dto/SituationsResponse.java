package com.kustaurant.dataprocessor.aianalysis.infrastructure.ai.dto;

import java.util.List;

public record SituationsResponse(
        List<String> situations,
        String reason
) {
}
