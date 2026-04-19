package com.kustaurant.restaurantSync.sync;

import com.kustaurant.map.ZoneType;

public record ZoneCrawlRequest(
        ZoneType crawlScope
) {}
