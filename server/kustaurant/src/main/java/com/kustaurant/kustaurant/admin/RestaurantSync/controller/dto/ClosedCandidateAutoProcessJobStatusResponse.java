package com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto;

public record ClosedCandidateAutoProcessJobStatusResponse(
        String jobId,
        String status,
        int total,
        int processed,
        int autoClosedCount,
        int recrawledCount,
        int failedCount,
        boolean done
) {
}
