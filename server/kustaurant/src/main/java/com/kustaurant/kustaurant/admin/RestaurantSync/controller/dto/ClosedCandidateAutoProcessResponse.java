package com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto;

public record ClosedCandidateAutoProcessResponse(
        int totalPendingClosed,
        int autoClosedCount,
        int recrawledCount,
        int failedCount
) {
}
