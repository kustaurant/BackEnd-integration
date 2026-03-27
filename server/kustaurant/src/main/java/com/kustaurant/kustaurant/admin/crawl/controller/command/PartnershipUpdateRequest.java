package com.kustaurant.kustaurant.admin.crawl.controller.command;

public record PartnershipUpdateRequest(
        Long restaurantId,
        String restaurantName,
        String benefit,
        String locationText
) {
}
