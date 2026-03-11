package com.kustaurant.kustaurant.admin.crawl;

public record IgImportResult(
        int matchedRestaurantCount,
        int unmatchedRestaurantCount
) {
}
