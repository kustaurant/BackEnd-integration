package com.kustaurant.kustaurant.admin.crawl.controller;

public record IgCrawlResponse(
        int crawledPages,
        int rawSavedCount,
        int matchedRestaurantCount,
        int unmatchedRestaurantCount
) {
}
