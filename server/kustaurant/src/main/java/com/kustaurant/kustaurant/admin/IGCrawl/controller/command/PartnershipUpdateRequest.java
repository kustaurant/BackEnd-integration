package com.kustaurant.kustaurant.admin.IGCrawl.controller.command;

public record PartnershipUpdateRequest(
        Long restaurantId,
        String restaurantName,
        String benefit,
        String locationText
) {
}
