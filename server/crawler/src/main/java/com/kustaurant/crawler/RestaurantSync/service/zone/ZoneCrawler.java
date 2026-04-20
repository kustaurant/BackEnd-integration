package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.crawler.RestaurantSync.CrawlGrid;
import com.kustaurant.crawler.RestaurantSync.GridGenerator;
import com.kustaurant.crawler.RestaurantSync.service.single.RestaurantSingleCrawler;
import com.kustaurant.map.ZonePolygon;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.restaurantSync.sync.ZoneCrawlResultPayload;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoneCrawler {
   private static final double DEFAULT_LAT_STEP = 0.0014;
   private static final double DEFAULT_LNG_STEP = 0.0028;
   private static final int DEFAULT_ZOOM = 19;
   private static final int MAX_PLACE_IDS = Integer.MAX_VALUE;
   private static final long INTER_PLACE_DELAY_MIN_MS = 3_000L;
   private static final long INTER_PLACE_DELAY_MAX_MS = 5_000L;

   private final RestaurantSingleCrawler restaurantSingleCrawler;
   private final ZonePlaceIdCollector zonePlaceIdCollector;
   private final ZoneResultPolicy zoneResultPolicy;

   public ZoneCrawlResultPayload crawlByScope(
           ZoneType crawlScope,
           Consumer<ZoneCrawlProgress> progressListener
   ) {
      ZonePolygon zone = zoneResultPolicy.findZonePolygon(crawlScope)
              .orElseThrow(() -> new IllegalArgumentException("Unsupported crawl scope: " + crawlScope));

      List<CrawlGrid> grids = GridGenerator.generate(zone, DEFAULT_LAT_STEP, DEFAULT_LNG_STEP);
      log.info(
              "구역 크롤 시작. scope={}, zoneType={}, gridCount={}, latStep={}, lngStep={}, zoom={}",
              crawlScope,
              zone.zoneType(),
              grids.size(),
              DEFAULT_LAT_STEP,
              DEFAULT_LNG_STEP,
              DEFAULT_ZOOM
      );

      Set<String> placeIds = zonePlaceIdCollector.discoverPlaceIds(
              grids,
              DEFAULT_ZOOM,
              MAX_PLACE_IDS,
              progress -> progressListener.accept(new ZoneCrawlProgress(
                      "DISCOVERING",
                      grids.size(),
                      progress.processedGridCount(),
                      progress.discoveredPlaceCount(),
                      0,
                      0,
                      0,
                      0,
                      List.of(),
                      null,
                      progress.currentGrid(),
                      null
              ))
      );

      List<RestaurantRaw> results = new ArrayList<>();
      Set<String> retryQueue = new LinkedHashSet<>();
      Set<String> finalFailedPlaceIds = new LinkedHashSet<>();
      int crawlAttempt = 0;

      for (String placeId : placeIds) {
         crawlAttempt++;
         progressListener.accept(new ZoneCrawlProgress(
                 "CRAWLING",
                 grids.size(),
                 grids.size(),
                 placeIds.size(),
                 placeIds.size(),
                 crawlAttempt,
                 results.size(),
                 finalFailedPlaceIds.size(),
                 List.copyOf(finalFailedPlaceIds),
                 null,
                 null,
                 placeId
         ));

         String placeUrl = "https://map.naver.com/p/entry/place/" + placeId;
         try {
            RestaurantRaw result = restaurantSingleCrawler.crawl(placeUrl);
            if (zoneResultPolicy.isCompleteFailure(result)) {
               retryQueue.add(placeId);
               continue;
            }
            if (!zoneResultPolicy.isMeaningfulResult(result)) {
               continue;
            }
            if (!zoneResultPolicy.isPlaceInsideZone(result, zone)) {
               log.info(
                       "구역 밖 데이터 제외. scope={}, placeId={}, placeName={}, inZone={}",
                       crawlScope,
                       placeId,
                       result.placeName(),
                       false
               );
               continue;
            }

            results.add(result);
            progressListener.accept(new ZoneCrawlProgress(
                    "CRAWLING",
                    grids.size(),
                    grids.size(),
                    placeIds.size(),
                    placeIds.size(),
                    crawlAttempt,
                    results.size(),
                    finalFailedPlaceIds.size(),
                    List.copyOf(finalFailedPlaceIds),
                    result,
                    null,
                    placeId
            ));
         } catch (Exception e) {
            retryQueue.add(placeId);
            log.warn("구역 크롤 상세 수집 실패. placeId={}, scope={}", placeId, crawlScope, e);
         } finally {
            sleepMillis(randomInterPlaceDelayMs());
         }
      }

      if (!retryQueue.isEmpty()) {
         List<String> retryTargets = new ArrayList<>(retryQueue);
         int totalWithRetry = placeIds.size() + retryTargets.size();

         for (String placeId : retryTargets) {
            crawlAttempt++;
            progressListener.accept(new ZoneCrawlProgress(
                    "RETRYING",
                    grids.size(),
                    grids.size(),
                    placeIds.size(),
                    totalWithRetry,
                    crawlAttempt,
                    results.size(),
                    finalFailedPlaceIds.size(),
                    List.copyOf(finalFailedPlaceIds),
                    null,
                    null,
                    placeId
            ));

            String placeUrl = "https://map.naver.com/p/entry/place/" + placeId;
            try {
               RestaurantRaw result = restaurantSingleCrawler.crawl(placeUrl);
               if (zoneResultPolicy.isCompleteFailure(result)) {
                  finalFailedPlaceIds.add(placeId);
                  continue;
               }
               if (!zoneResultPolicy.isMeaningfulResult(result)) {
                  continue;
               }
               if (!zoneResultPolicy.isPlaceInsideZone(result, zone)) {
                  log.info(
                          "구역 밖 데이터 제외(리트라이). scope={}, placeId={}, placeName={}, inZone={}",
                          crawlScope,
                          placeId,
                          result.placeName(),
                          false
                  );
                  continue;
               }

               results.add(result);
               progressListener.accept(new ZoneCrawlProgress(
                       "RETRYING",
                       grids.size(),
                       grids.size(),
                       placeIds.size(),
                       totalWithRetry,
                       crawlAttempt,
                       results.size(),
                       finalFailedPlaceIds.size(),
                       List.copyOf(finalFailedPlaceIds),
                       result,
                       null,
                       placeId
               ));
            } catch (Exception e) {
               finalFailedPlaceIds.add(placeId);
               log.warn("2차 리트라이 예외 최종실패. scope={}, placeId={}", crawlScope, placeId, e);
            } finally {
               sleepMillis(randomInterPlaceDelayMs());
            }
         }
      }

      progressListener.accept(new ZoneCrawlProgress(
              "COMPLETED",
              grids.size(),
              grids.size(),
              placeIds.size(),
              placeIds.size() + retryQueue.size(),
              crawlAttempt,
              results.size(),
              finalFailedPlaceIds.size(),
              List.copyOf(finalFailedPlaceIds),
              null,
              null,
              null
      ));

      return new ZoneCrawlResultPayload(placeIds.size(), results.size(), results);
   }

   private long randomInterPlaceDelayMs() {
      return ThreadLocalRandom.current().nextLong(INTER_PLACE_DELAY_MIN_MS, INTER_PLACE_DELAY_MAX_MS + 1);
   }

   private void sleepMillis(long millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   public record ZoneCrawlProgress(
           String phase,
           int totalGridCount,
           int processedGridCount,
           int discoveredPlaceCount,
           int totalPlaceCount,
           int attemptedPlaceCount,
           int crawledSuccessCount,
           int finalFailedCount,
           List<String> finalFailedPlaceIds,
           RestaurantRaw acceptedResult,
           String currentGrid,
           String currentPlaceId
   ) {
   }
}
