package com.kustaurant.mainapp.post.post.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostScrapResponse(
        @Schema(description = "스크랩 상태", example = "true")
        Boolean isScrapped,
        @Schema(description = "스크랩 수", example = "10")
        Integer postScrapCount

) {}