package com.kustaurant.crawler.RestaurantSync;

import com.kustaurant.map.ZoneType;

public record CrawlGrid(
        ZoneType zoneType,
        int row,
        int col,
        double centerLat,
        double centerLng
) {}
