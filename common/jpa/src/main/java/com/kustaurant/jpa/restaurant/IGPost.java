package com.kustaurant.jpa.restaurant;

public record IGPost(
        String postUrl,
        String restaurantName,
        String benefit,
        String location,
        String phoneNumber
) {}
