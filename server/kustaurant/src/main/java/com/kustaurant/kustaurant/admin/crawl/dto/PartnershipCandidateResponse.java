package com.kustaurant.kustaurant.admin.crawl.dto;

import java.util.List;

public record PartnershipCandidateResponse(
        Long partnershipId,
        String rawRestaurantName,
        String rawLocationText,
        String benefit,
        List<CandidateItem> candidates
) {
    public record CandidateItem(
            Long restaurantId,
            String restaurantName,
            String address,
            String phoneNumber
    ) {
    }
}