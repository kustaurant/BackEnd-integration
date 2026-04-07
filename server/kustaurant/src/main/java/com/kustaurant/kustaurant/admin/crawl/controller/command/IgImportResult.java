package com.kustaurant.kustaurant.admin.crawl.controller.command;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public record IgImportResult(
        int matchedRestaurantCount,
        int unmatchedRestaurantCount
) {
}
