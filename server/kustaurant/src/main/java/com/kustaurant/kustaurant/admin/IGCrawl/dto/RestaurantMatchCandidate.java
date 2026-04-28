package com.kustaurant.kustaurant.admin.IGCrawl.dto;

public record RestaurantMatchCandidate(
        Long id,
        String name,
        String address,
        String phoneNumber
) {
}