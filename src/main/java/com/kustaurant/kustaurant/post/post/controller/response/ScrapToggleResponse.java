package com.kustaurant.kustaurant.post.post.controller.response;

import com.kustaurant.kustaurant.post.post.domain.enums.ScrapStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record ScrapToggleResponse(
        @Schema(description = "스크랩 수", example = "10")
        Integer postScrapCount,
        @Schema(description = "현재 상태", example = "SCRAPPED")
        ScrapStatus status
) {}