package com.kustaurant.kustaurant.post.post.controller.response;

import com.kustaurant.kustaurant.post.post.domain.enums.ScrapStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapToggleResponse {
    public ScrapToggleResponse(Integer scrapCount, ScrapStatus status) {
        this.scrapCount = scrapCount;
        this.status = status;
    }

    @Schema(description = "스크랩 수", example = "10")
    Integer scrapCount;

    @Schema(description = "현재 상태", example = "SCRAPPED")
    ScrapStatus status;
}