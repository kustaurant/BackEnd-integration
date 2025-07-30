package com.kustaurant.kustaurant.admin.adminPage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantListResponse {
    private Integer restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPosition;
    private String restaurantType;
    private String status;
    private Integer restaurantEvaluationCount;
    private LocalDateTime createdAt;
}