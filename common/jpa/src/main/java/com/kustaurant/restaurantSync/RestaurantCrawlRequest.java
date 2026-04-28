package com.kustaurant.restaurantSync;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RestaurantCrawlRequest(
        @NotBlank(message = "placeId 값이 비어있습니다.")
        @Pattern(regexp = "\\d+", message = "placeId는 숫자만 입력해야 합니다.")
        String placeId
) {
    public String normalizedPlaceUrl() {
        String value = placeId == null ? null : placeId.trim();
        if (value == null || value.isBlank()) {
            return value;
        }
        return "https://map.naver.com/p/entry/place/" + value;
    }
}
