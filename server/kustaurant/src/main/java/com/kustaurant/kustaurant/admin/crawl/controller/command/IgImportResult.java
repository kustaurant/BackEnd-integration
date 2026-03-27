package com.kustaurant.kustaurant.admin.crawl.controller.command;

public record IgImportResult(
        int matchedRestaurantCount,
        int unmatchedRestaurantCount
) {
}
