package com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto;

import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateStatus;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateType;
import java.time.LocalDateTime;

public record RestaurantSyncCandidateResponse(
        Long id,
        String placeId,
        String restaurantName,
        String restaurantLink,
        SyncCandidateType candidateType,
        SyncCandidateStatus candidateStatus,
        String reason,
        String reviewedBy,
        LocalDateTime reviewedAt,
        LocalDateTime appliedAt,
        LocalDateTime createdAt
) {
}
