package com.kustaurant.naverplace.sync;

import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import java.util.List;

public record NaverPlaceZoneCrawlResult(
        CrawlScopeType crawlScope,
        int discoveredPlaceCount,
        int successCount,
        List<NaverPlaceCrawlResult> results
) {
}
