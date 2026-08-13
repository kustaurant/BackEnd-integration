package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.crawler.RestaurantSync.CrawlGrid;
import com.kustaurant.crawler.RestaurantSync.GridGenerator;
import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.kustaurant.crawler.RestaurantSync.service.single.RestaurantSingleCrawler;
import com.kustaurant.map.ZonePolygon;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.restaurantSync.sync.ZoneCrawlResultPayload;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoneCrawler {
   private final PlaywrightManager playwrightManager;
   private final RestaurantSingleCrawler restaurantSingleCrawler;
   private final ZonePlaceIdCollector zonePlaceIdCollector;
   private final ZoneResultPolicy zoneResultPolicy;

   @Value("${crawler.naver-place.max-grids:2147483647}")
   private int maxGrids = Integer.MAX_VALUE;

   @Value("${crawler.naver-place.max-place-ids:2147483647}")
   private int maxPlaceIds = Integer.MAX_VALUE;

   public ZoneCrawlResultPayload crawlByScope(
           ZoneType crawlScope,
           Consumer<ZoneCrawlProgress> progressListener
   ) {
      return crawlByScope(crawlScope, new ZoneCrawlCheckpoint(), progressListener);
   }

   ZoneCrawlResultPayload crawlByScope(
           ZoneType crawlScope,
           ZoneCrawlCheckpoint checkpoint,
           Consumer<ZoneCrawlProgress> progressListener
   ) {
      return playwrightManager.withReusableBrowser(
              () -> crawlByScopeWithReusableBrowser(crawlScope, checkpoint, progressListener)
      );
   }

   private ZoneCrawlResultPayload crawlByScopeWithReusableBrowser(
           ZoneType crawlScope,
           ZoneCrawlCheckpoint checkpoint,
           Consumer<ZoneCrawlProgress> progressListener
   ) {
      ZonePolygon zone = zoneResultPolicy.findZonePolygon(crawlScope)
              .orElseThrow(() -> new IllegalArgumentException("Unsupported crawl scope: " + crawlScope));

      List<CrawlGrid> generatedGrids = GridGenerator.generate(
              zone,
              ZoneCrawlDefaults.LAT_STEP,
              ZoneCrawlDefaults.LNG_STEP
      );
      int gridLimit = Math.max(1, Math.min(maxGrids, generatedGrids.size()));
      List<CrawlGrid> grids = List.copyOf(generatedGrids.subList(0, gridLimit));
      log.info(
              "구역 크롤 시작. scope={}, zoneType={}, gridCount={}, latStep={}, lngStep={}, zoom={}",
              crawlScope,
              zone.zoneType(),
              grids.size(),
              ZoneCrawlDefaults.LAT_STEP,
              ZoneCrawlDefaults.LNG_STEP,
              ZoneCrawlDefaults.ZOOM
      );

      int placeIdLimit = Math.max(1, maxPlaceIds);
      int resumeGridIndex = checkpoint.nextGridIndex();
      if (resumeGridIndex > grids.size()) {
         throw new IllegalStateException(
                 "체크포인트가 현재 그리드 범위를 벗어났습니다. nextGridIndex="
                         + resumeGridIndex + ", gridCount=" + grids.size()
         );
      }

      if (resumeGridIndex > 0) {
         log.info(
                 "구역 크롤 체크포인트 재개. scope={}, nextGridIndex={}, preservedPlaceCount={}",
                 crawlScope,
                 resumeGridIndex,
                 checkpoint.discoveredPlaceCount()
         );
      }

      for (int gridIndex = resumeGridIndex; gridIndex < grids.size(); gridIndex++) {
         if (checkpoint.discoveredPlaceCount() >= placeIdLimit) {
            break;
         }

         CrawlGrid grid = grids.get(gridIndex);
         Set<String> found = zonePlaceIdCollector.discoverPlaceIdsFromGrid(grid, ZoneCrawlDefaults.ZOOM);
         int before = checkpoint.discoveredPlaceCount();
         checkpoint.completeGrid(gridIndex, found, placeIdLimit);
         int after = checkpoint.discoveredPlaceCount();

         log.info(
                 "그리드 체크포인트 저장. scope={}, row={}, col={}, found={}, added={}, duplicate={}, total={}",
                 crawlScope,
                 grid.row(),
                 grid.col(),
                 found.size(),
                 after - before,
                 found.size() - (after - before),
                 after
         );

         progressListener.accept(new ZoneCrawlProgress(
                 "DISCOVERING",
                 grids.size(),
                 checkpoint.nextGridIndex(),
                 checkpoint.discoveredPlaceCount(),
                 0,
                 0,
                 0,
                 0,
                 List.of(),
                 null,
                 grid.row() + "," + grid.col(),
                 null
         ));
      }

      Set<String> placeIds = checkpoint.discoveredPlaceIdsSnapshot();

      List<RestaurantRaw> results = new ArrayList<>();
      Set<String> retryQueue = new LinkedHashSet<>();
      Set<String> finalFailedPlaceIds = new LinkedHashSet<>();
      int crawlAttempt = 0;

      for (String placeId : placeIds) {
         crawlAttempt++;
         progressListener.accept(new ZoneCrawlProgress(
                 "CRAWLING",
                 grids.size(),
                 checkpoint.nextGridIndex(),
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
                    checkpoint.nextGridIndex(),
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
                    checkpoint.nextGridIndex(),
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
                       checkpoint.nextGridIndex(),
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
            }
         }
      }

      progressListener.accept(new ZoneCrawlProgress(
              "COMPLETED",
              grids.size(),
              checkpoint.nextGridIndex(),
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

}
