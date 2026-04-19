package com.kustaurant.restaurantSync.sync;

import com.kustaurant.restaurantSync.RestaurantRaw;
import java.util.List;

public record ZoneCrawlJobResultsPayload(
        // 서버 간 통신용 -> 배치 저장 처리
        int nextIndex,
        int totalBufferedCount,
        List<RestaurantRaw> results
) {
}
