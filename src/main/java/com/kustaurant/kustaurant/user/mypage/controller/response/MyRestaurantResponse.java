package com.kustaurant.kustaurant.user.mypage.controller.response;

public record MyRestaurantResponse(
        String restaurantName,
        Integer restaurantId,
        String restaurantImgURL,
        Integer mainTier,
        String restaurantType,
        String restaurantPosition
) {}
