package com.kustaurant.restauranttier.tab4_community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapToggleDTO

{
    public ScrapToggleDTO(Integer scrapCount, String status) {
        this.scrapCount = scrapCount;
        this.status = status;
    }

    @Schema(description = "스크랩 수", example = "10")
    Integer scrapCount;

    @Schema(description = "상태", example = "scrapCreated")
    String status;
}