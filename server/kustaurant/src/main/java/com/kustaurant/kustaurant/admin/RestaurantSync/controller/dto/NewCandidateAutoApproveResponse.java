package com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto;

public record NewCandidateAutoApproveResponse(
        int totalPendingNew,
        int approvedCount,
        int failedCount
) {
}
