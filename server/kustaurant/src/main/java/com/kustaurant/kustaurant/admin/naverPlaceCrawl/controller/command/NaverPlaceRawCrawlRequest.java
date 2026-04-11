package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command;

import jakarta.validation.constraints.NotBlank;

public record NaverPlaceRawCrawlRequest(
        @NotBlank String placeUrl
) {}
