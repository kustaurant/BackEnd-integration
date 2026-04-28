package com.kustaurant.kustaurant.admin.RestaurantCrawl.service;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.dto.ZoneCrawlResultResponse;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.restaurantSync.sync.ZoneCrawlResultPayload;
import java.util.List;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ZoneCrawlTestService {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(ZoneCrawlTestService.class);
   private final RestaurantCrawlerClient crawlerClient;
   private final RestaurantRawSaveService rawSaveService;


   public ZoneCrawlResultResponse crawlByScopeTest(ZoneType crawlScope) {
      log.info("zone sync test started. scope={}", crawlScope);
      ZoneCrawlResultPayload zoneResult = this.crawlerClient.crawlZoneTest(crawlScope);

      return processZoneResult(crawlScope, zoneResult, false);
   }

   private ZoneCrawlResultResponse processZoneResult(ZoneType crawlScope, ZoneCrawlResultPayload zoneResult, boolean persistRaw) {
      List<RestaurantRaw> crawlResults = zoneResult.results() == null ? List.of() : zoneResult.results();
      log.info("zone sync crawler response received. scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, resultPayloadSize={}, persistRaw={}", new Object[]{crawlScope, zoneResult.discoveredPlaceCount(), zoneResult.successCount(), crawlResults.size(), persistRaw});
      if (!persistRaw) {
         for(RestaurantRaw result : crawlResults) {
            if (result != null) {
               log.info("zone sync test result. scope={}, placeId={}, placeName={}, lat={}, lng={}, menuCount={}", new Object[]{crawlScope, result.sourcePlaceId(), result.placeName(), result.latitude(), result.longitude(), result.menus() == null ? 0 : result.menus().size()});
            }
         }

         log.info("zone sync test finished without raw save. scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, savedRawCount=0", new Object[]{crawlScope, zoneResult.discoveredPlaceCount(), zoneResult.successCount()});
         return new ZoneCrawlResultResponse(zoneResult.discoveredPlaceCount(), zoneResult.successCount(), 0);
      } else {
         int savedCount = 0;

         for(RestaurantRaw result : crawlResults) {
            try {
               this.rawSaveService.saveResult(result, crawlScope);
               ++savedCount;
               log.info("zone sync raw saved. scope={}, placeId={}, savedCount={}", new Object[]{crawlScope, result.sourcePlaceId(), savedCount});
            } catch (Exception e) {
               log.warn("failed to save zone crawl raw. scope={}, placeId={}", new Object[]{crawlScope, result.sourcePlaceId(), e});
            }
         }

         log.info("zone sync finished. scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, savedRawCount={}", new Object[]{crawlScope, zoneResult.discoveredPlaceCount(), zoneResult.successCount(), savedCount});
         return new ZoneCrawlResultResponse(zoneResult.discoveredPlaceCount(), zoneResult.successCount(), savedCount);
      }
   }
}
