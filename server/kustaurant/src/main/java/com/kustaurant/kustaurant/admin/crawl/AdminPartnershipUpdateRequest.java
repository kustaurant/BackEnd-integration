package com.kustaurant.kustaurant.admin.crawl;

import com.kustaurant.jpa.restaurant.enums.MatchStatus;

public record AdminPartnershipUpdateRequest(
        Long restaurantId,
        String restaurantName,
        String benefit,
        String locationText,
        String contactPhone,
        MatchStatus matchStatus
) {
}
