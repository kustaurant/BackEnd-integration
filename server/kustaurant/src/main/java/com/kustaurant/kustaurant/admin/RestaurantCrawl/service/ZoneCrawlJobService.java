package com.kustaurant.kustaurant.admin.RestaurantCrawl.service;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.dto.ZoneCrawlJobStatusResponse;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.restaurantSync.sync.CrawlJobIdResponse;
import com.kustaurant.restaurantSync.sync.ZoneCrawlStatusPayload;
import com.kustaurant.restaurantSync.sync.ZoneCrawlJobResultsPayload;
import com.kustaurant.restaurantSync.sync.ZoneJCrawlobStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZoneCrawlJobService {
   private static final int RAW_SAVE_BATCH_SIZE = 10;
   private static final int RESULT_FETCH_LIMIT = 100;
   private static final long CRAWLER_POLL_INTERVAL_MS = 20_000L;

   private final RestaurantCrawlerClient crawlerClient;
   private final RestaurantRawSaveService rawSaveService;

   private final Map<String, ZoneCrawlJobState> jobs = new ConcurrentHashMap<>();
   private final ExecutorService executor = Executors.newCachedThreadPool();

   public CrawlJobIdResponse start(ZoneType crawlScope) {
      String jobId = UUID.randomUUID().toString();
      ZoneCrawlJobState state = ZoneCrawlJobState.pending(jobId, crawlScope);
      jobs.put(jobId, state);
      executor.submit(() -> runJob(state));

      log.info("구역 동기화 작업 등록. jobId={}, scope={}", jobId, crawlScope);
      return new CrawlJobIdResponse(jobId);
   }

   public Optional<ZoneCrawlJobStatusResponse> getStatus(String jobId) {
      return Optional.ofNullable(jobs.get(jobId)).map(ZoneCrawlJobState::toResponse);
   }

   private void runJob(ZoneCrawlJobState state) {
      state.markRunning();
      try {
         state.currentPhase = "CRAWL_JOB_START";
         CrawlJobIdResponse startResponse = crawlerClient.startZoneCrawlJob(state.crawlScope);
         state.crawlerJobId = startResponse.jobId();
         state.currentPhase = "CRAWL_RUNNING";
         int nextResultIndex = 0;

         while (true) {
            ZoneCrawlStatusPayload crawlStatus = crawlerClient.getZoneCrawlJobStatus(state.crawlerJobId);
            state.applyCrawlerStatus(crawlStatus);

            state.currentPhase = "SAVE_RAW_STREAMING";
            nextResultIndex = fetchAndSaveIncrementalResults(state, nextResultIndex);

            if (crawlStatus.status() == ZoneJCrawlobStatus.SUCCESS) {
               state.currentPhase = "SAVE_RAW_FINALIZE";
               nextResultIndex = fetchAndSaveIncrementalResults(state, nextResultIndex);

               state.markSuccess();
               log.info(
                       "구역 동기화 작업 완료. jobId={}, scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, savedRawCount={}, saveFailedCount={}, lastSavedIndex={}",
                       state.jobId, state.crawlScope, state.discoveredPlaceCount, state.crawledSuccessCount,
                       state.savedRawCount, state.saveFailedCount, nextResultIndex
               );
               break;
            }

            if (crawlStatus.status() == ZoneJCrawlobStatus.FAILED) {
               throw new IllegalStateException("크롤 작업 실패: " + crawlStatus.errorMessage());
            }

            sleep(CRAWLER_POLL_INTERVAL_MS);
         }
      } catch (Exception e) {
         state.markFailed(e);
         log.warn(
                 "구역 동기화 작업 실패. jobId={}, scope={}, message={}",
                 state.jobId,
                 state.crawlScope,
                 e.getMessage(),
                 e
         );
      }
   }

   private int fetchAndSaveIncrementalResults(ZoneCrawlJobState state, int fromIndex) {
      int nextIndex = fromIndex;

      while (true) {
         ZoneCrawlJobResultsPayload partial =
                 crawlerClient.getZoneCrawlJobResults(state.crawlerJobId, nextIndex, RESULT_FETCH_LIMIT);
         List<RestaurantRaw> crawlResults = partial.results() == null ? List.of() : partial.results();
         if (crawlResults.isEmpty()) {
            return partial.nextIndex();
         }

         for (int i = 0; i < crawlResults.size(); i += RAW_SAVE_BATCH_SIZE) {
            int end = Math.min(i + RAW_SAVE_BATCH_SIZE, crawlResults.size());
            List<RestaurantRaw> batch = crawlResults.subList(i, end);

            if (!batch.isEmpty()) {
               RestaurantRaw last = batch.get(batch.size() - 1);
               state.currentPlaceId = last == null ? null : last.sourcePlaceId();
            }

            RestaurantRawSaveService.BatchSaveResult batchResult = rawSaveService.saveResultsBatch(batch, state.crawlScope);
            state.savedRawCount += batchResult.savedCount();
            state.saveFailedCount += batchResult.failedCount();

            if (batchResult.failedCount() > 0) {
               log.warn(
                       "구역 동기화 raw 배치 저장 일부 실패. jobId={}, scope={}, globalRange={}~{}, failedCount={}, failedPlaceIds={}",
                       state.jobId,
                       state.crawlScope,
                       nextIndex + i,
                       nextIndex + end - 1,
                       batchResult.failedCount(),
                       batchResult.failedPlaceIds()
               );
            }
         }

         nextIndex = partial.nextIndex();
         if (nextIndex >= partial.totalBufferedCount()) {
            return nextIndex;
         }
      }
   }

   private void sleep(long millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   private static final class ZoneCrawlJobState {
      private final String jobId;
      private final ZoneType crawlScope;

      private volatile ZoneJCrawlobStatus status;
      private volatile String crawlerJobId;
      private volatile String currentPhase;
      private volatile int totalGridCount;
      private volatile int processedGridCount;
      private volatile int discoveredPlaceCount;
      private volatile int attemptedPlaceCount;
      private volatile int crawledSuccessCount;
      private volatile int finalFailedCount;
      private volatile List<String> finalFailedPlaceIds = List.of();
      private volatile int savedRawCount;
      private volatile int saveFailedCount;
      private volatile String currentGrid;
      private volatile String currentPlaceId;
      private volatile String errorMessage;
      private volatile LocalDateTime startedAt;
      private volatile LocalDateTime finishedAt;

      private ZoneCrawlJobState(String jobId, ZoneType crawlScope) {
         this.jobId = jobId;
         this.crawlScope = crawlScope;
         this.status = ZoneJCrawlobStatus.PENDING;
      }

      private static ZoneCrawlJobState pending(String jobId, ZoneType crawlScope) {
         return new ZoneCrawlJobState(jobId, crawlScope);
      }

      private void markRunning() {
         this.status = ZoneJCrawlobStatus.RUNNING;
         this.currentPhase = "QUEUED";
         this.startedAt = LocalDateTime.now();
      }

      private void markSuccess() {
         this.status = ZoneJCrawlobStatus.SUCCESS;
         this.currentPhase = "COMPLETED";
         this.finishedAt = LocalDateTime.now();
      }

      private void markFailed(Exception e) {
         this.status = ZoneJCrawlobStatus.FAILED;
         this.errorMessage = e.getMessage();
         this.finishedAt = LocalDateTime.now();
      }

      private void applyCrawlerStatus(ZoneCrawlStatusPayload crawlStatus) {
         this.totalGridCount = crawlStatus.totalGridCount();
         this.processedGridCount = crawlStatus.processedGridCount();
         this.discoveredPlaceCount = crawlStatus.discoveredPlaceCount();
         this.attemptedPlaceCount = crawlStatus.attemptedPlaceCount();
         this.crawledSuccessCount = crawlStatus.crawledSuccessCount();
         this.finalFailedCount = crawlStatus.finalFailedCount();
         this.finalFailedPlaceIds = crawlStatus.finalFailedPlaceIds();
         this.currentGrid = crawlStatus.currentGrid();
         this.currentPlaceId = crawlStatus.currentPlaceId();
      }

      private ZoneCrawlJobStatusResponse toResponse() {
         return new ZoneCrawlJobStatusResponse(
                 crawlScope,
                 status,
                 currentPhase,
                 totalGridCount,
                 processedGridCount,
                 discoveredPlaceCount,
                 attemptedPlaceCount,
                 crawledSuccessCount,
                 finalFailedCount,
                 finalFailedPlaceIds,
                 savedRawCount,
                 saveFailedCount,
                 currentGrid,
                 currentPlaceId,
                 errorMessage
         );
      }
   }
}
