package com.kustaurant.restaurantSync.sync;

import com.kustaurant.restaurantSync.RestaurantRaw;
import java.util.List;

public record ZoneCrawlResultPayload(
        // 서버 간 통신용 -> 쿠스토랑 응답반환 객체 생성 참고용
        int discoveredPlaceCount,
        int successCount,
        List<RestaurantRaw> results
) {
}
