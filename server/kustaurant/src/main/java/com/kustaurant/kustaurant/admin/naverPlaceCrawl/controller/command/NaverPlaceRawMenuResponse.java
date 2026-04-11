package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command;

public record NaverPlaceRawMenuResponse(
        String menuName,
        String menuPrice,
        String menuImageUrl
) {}
