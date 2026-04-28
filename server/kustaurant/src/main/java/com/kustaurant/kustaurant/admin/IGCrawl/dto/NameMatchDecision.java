package com.kustaurant.kustaurant.admin.IGCrawl.dto;

import com.kustaurant.restaurant.enums.MatchStatus;

public record NameMatchDecision(
        Long restaurantId,
        MatchStatus matchStatus,
        String reason
) {
    public static NameMatchDecision matched(Long restaurantId, String reason) {
        return new NameMatchDecision(restaurantId, MatchStatus.MATCHED, reason);
    }

    public static NameMatchDecision unmatched(String reason) {
        return new NameMatchDecision(null, MatchStatus.UNMATCHED, reason);
    }

    public boolean isMatched() {
        return restaurantId != null && matchStatus == MatchStatus.MATCHED;
    }
}