package com.kustaurant.kustaurant.admin.IGCrawl.controller.query;

import java.time.LocalDateTime;

public record PartnershipListResponse(
        Long id,
        Long restaurantId,
        String restaurantName,
        String target,
        String benefit,
        String status,
        String url,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}