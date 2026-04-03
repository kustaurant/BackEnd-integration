package com.kustaurant.kustaurant.admin.crawl.dto;

public record RestaurantMatchCandidate(
        Long id,
        String name,
        String address,
        String phoneNumber
) {
}