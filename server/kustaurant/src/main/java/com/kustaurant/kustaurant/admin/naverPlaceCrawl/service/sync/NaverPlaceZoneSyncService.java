package com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.sync;

import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync.NaverPlaceZoneSyncResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.NaverPlaceRawSaveService;
import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlResult;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NaverPlaceZoneSyncService {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(NaverPlaceZoneSyncService.class);
   private final RestaurantCrawlerClient crawlerClient;
   private final NaverPlaceRawSaveService rawSaveService;

   @Transactional
   public NaverPlaceZoneSyncResponse syncByScope(CrawlScopeType crawlScope) {
      log.info("zone sync started. scope={}", crawlScope);
      NaverPlaceZoneCrawlResult zoneResult = this.crawlerClient.crawlZone(crawlScope);
      return this.processZoneResult(crawlScope, zoneResult, true);
   }

   public NaverPlaceZoneSyncResponse syncByScopeTest(CrawlScopeType crawlScope) {
      log.info("zone sync test started. scope={}", crawlScope);
      NaverPlaceZoneCrawlResult zoneResult = this.crawlerClient.crawlZoneTest(crawlScope);
      return this.processZoneResult(crawlScope, zoneResult, false);
   }

   private NaverPlaceZoneSyncResponse processZoneResult(CrawlScopeType crawlScope, NaverPlaceZoneCrawlResult zoneResult, boolean persistRaw) {
      List<NaverPlaceCrawlResult> crawlResults = zoneResult.results() == null ? List.of() : zoneResult.results();
      log.info("zone sync crawler response received. scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, resultPayloadSize={}, persistRaw={}", new Object[]{crawlScope, zoneResult.discoveredPlaceCount(), zoneResult.successCount(), crawlResults.size(), persistRaw});
      if (!persistRaw) {
         for(NaverPlaceCrawlResult result : crawlResults) {
            if (result != null) {
               log.info("zone sync test result. scope={}, placeId={}, placeName={}, lat={}, lng={}, menuCount={}", new Object[]{crawlScope, result.sourcePlaceId(), result.placeName(), result.latitude(), result.longitude(), result.menus() == null ? 0 : result.menus().size()});
            }
         }

         log.info("zone sync test finished without raw save. scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, savedRawCount=0", new Object[]{crawlScope, zoneResult.discoveredPlaceCount(), zoneResult.successCount()});
         return new NaverPlaceZoneSyncResponse(crawlScope, zoneResult.discoveredPlaceCount(), zoneResult.successCount(), 0);
      } else {
         int savedCount = 0;

         for(NaverPlaceCrawlResult result : crawlResults) {
            try {
               this.rawSaveService.saveResult(result, crawlScope);
               ++savedCount;
               log.info("zone sync raw saved. scope={}, placeId={}, savedCount={}", new Object[]{crawlScope, result.sourcePlaceId(), savedCount});
            } catch (Exception e) {
               log.warn("failed to save zone crawl raw. scope={}, placeId={}", new Object[]{crawlScope, result.sourcePlaceId(), e});
            }
         }

         log.info("zone sync finished. scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, savedRawCount={}", new Object[]{crawlScope, zoneResult.discoveredPlaceCount(), zoneResult.successCount(), savedCount});
         return new NaverPlaceZoneSyncResponse(crawlScope, zoneResult.discoveredPlaceCount(), zoneResult.successCount(), savedCount);
      }
   }

   @Generated
   public NaverPlaceZoneSyncService(final RestaurantCrawlerClient crawlerClient, final NaverPlaceRawSaveService rawSaveService) {
      this.crawlerClient = crawlerClient;
      this.rawSaveService = rawSaveService;
   }
}
