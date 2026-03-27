package com.kustaurant.kustaurant.admin.crawl.controller.command;

public record IgCrawlResponse(
        int crawledPages,
        int rawSavedCount,
        int matchedRestaurantCount,
        int unmatchedRestaurantCount
) {
}
