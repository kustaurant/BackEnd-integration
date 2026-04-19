package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.crawler.RestaurantSync.CrawlGrid;
import com.kustaurant.crawler.RestaurantSync.GridGenerator;
import com.kustaurant.crawler.RestaurantSync.service.single.RestaurantSingleCrawler;
import com.kustaurant.map.ZonePolygon;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.restaurantSync.sync.ZoneCrawlResultPayload;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoneTestCrawler {

   private static final double DEFAULT_LAT_STEP = 0.0018;
   private static final double DEFAULT_LNG_STEP = 0.0022;
   private static final int DEFAULT_ZOOM = 19;
   private static final int MAX_CRAWL_COUNT = 10;

   private final RestaurantSingleCrawler restaurantSingleCrawler;
   private final ZonePlaceIdCollector zonePlaceIdCollector;
   private final ZoneResultPolicy zoneResultPolicy;

   public ZoneCrawlResultPayload testCrawl(ZoneType crawlScope) {
      ZonePolygon zone = zoneResultPolicy.findZonePolygon(crawlScope)
              .orElseThrow(() -> new IllegalArgumentException("Unsupported crawl scope: " + crawlScope));

      List<CrawlGrid> grids = GridGenerator.generate(zone, DEFAULT_LAT_STEP, DEFAULT_LNG_STEP);
      if (grids.isEmpty()) {
         return new ZoneCrawlResultPayload(0, 0, List.of());
      }

      CrawlGrid firstGrid = grids.get(0);
      Set<String> collectedIds = zonePlaceIdCollector.discoverPlaceIdsFromGrid(firstGrid, DEFAULT_ZOOM);
      List<RestaurantRaw> results = new ArrayList<>();

      int crawled = 0;
      for (String placeId : collectedIds) {
         if (crawled >= MAX_CRAWL_COUNT) {
            break;
         }

         String placeUrl = "https://map.naver.com/p/entry/place/" + placeId;
         try {
            RestaurantRaw result = restaurantSingleCrawler.analyze(placeUrl);
            if (!zoneResultPolicy.isMeaningfulResult(result)) {
               log.info("구역 판별 스킵(유효 데이터 부족). scope={}, placeId={}", crawlScope, placeId);
               continue;
            }

            boolean inside = zoneResultPolicy.isPlaceInsideZone(result, zone);
            log.info(
                    "구역 포함 판별. scope={}, placeId={}, placeName={}, lat={}, lng={}, inside={}",
                    crawlScope, placeId, result.placeName(),
                    result.latitude(), result.longitude(), inside
            );
            if (!inside) continue;

            results.add(result);
            crawled++;
         } catch (Exception e) {
            log.warn("단일 그리드 상세 크롤 실패. placeId={}", placeId, e);
         } finally {
            long delayMs = ThreadLocalRandom.current().nextLong(3_000, 5_001);
            log.info(
                    "식당 간 분석 랜덤 대기. placeId={}, waitSec={}",
                    placeId,
                    delayMs / 1000.0
            );
            try {
               Thread.sleep(delayMs);
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
               break;
            }
         }
      }

      log.info(
              "구역 테스트 크롤 종료. scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, returnedResultCount={}",
              crawlScope, collectedIds.size(), crawled, results.size()
      );

      return new ZoneCrawlResultPayload(collectedIds.size(), results.size(), results);
   }
}
