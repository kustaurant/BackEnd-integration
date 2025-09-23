package com.kustaurant.mainapp.user.mypage.controller.response.api;

public record MyRestaurantResponse(
        String restaurantName,
        Long restaurantId,
        String restaurantImgURL,
        Integer mainTier,
        String restaurantType,
        String restaurantPosition
) {}
