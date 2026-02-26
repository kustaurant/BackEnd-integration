package com.kustaurant.kustaurant.admin.crawl.controller;

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