package com.kustaurant.kustaurant.admin.adminPage.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record RestaurantListResponse(
        Long restaurantId,
        String restaurantName,
        String restaurantAddress,
        String restaurantPosition,
        String restaurantType,
        String status,
        Long restaurantEvaluationCount,
        LocalDateTime createdAt
) { }