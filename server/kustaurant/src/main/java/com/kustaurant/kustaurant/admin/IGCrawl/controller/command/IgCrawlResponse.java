package com.kustaurant.kustaurant.admin.IGCrawl.controller.command;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public record IgCrawlResponse(
        int crawledPages,
        int rawSavedCount,
        int matchedRestaurantCount,
        int unmatchedRestaurantCount
) {
}
