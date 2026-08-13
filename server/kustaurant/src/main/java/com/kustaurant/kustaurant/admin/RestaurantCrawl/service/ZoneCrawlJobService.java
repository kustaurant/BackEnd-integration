package com.kustaurant.kustaurant.admin.RestaurantCrawl.service;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.dto.ZoneCrawlJobStatusResponse;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.restaurantSync.sync.CrawlJobIdResponse;
import com.kustaurant.restaurantSync.sync.ZoneCrawlStatusPayload;
import com.kustaurant.restaurantSync.sync.ZoneCrawlJobResultsPayload;
import com.kustaurant.restaurantSync.sync.ZoneCrawlJobStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jakarta.annotation.PreDestroy;
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
   private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "zone-crawl-sync-worker");
      thread.setDaemon(true);
      return thread;
   });

   @PreDestroy
   void shutdownExecutor() {
      executor.shutdownNow();
   }

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

   public Optional<CrawlJobIdResponse> resume(String jobId) {
      ZoneCrawlJobState state = jobs.get(jobId);
      if (state == null) {
         return Optional.empty();
      }

      state.markResumeQueued();
      executor.submit(() -> runJob(state, true));
      log.info("구역 동기화 작업 재개 등록. jobId={}, crawlerJobId={}", jobId, state.crawlerJobId);
      return Optional.of(new CrawlJobIdResponse(jobId));
   }

   private void runJob(ZoneCrawlJobState state) {
      runJob(state, false);
   }

   private void runJob(ZoneCrawlJobState state, boolean resume) {
      state.markRunning();
      try {
         if (resume) {
            state.currentPhase = "CRAWL_JOB_RESUME";
            crawlerClient.resumeZoneCrawlJob(state.crawlerJobId);
         } else {
            state.currentPhase = "CRAWL_JOB_START";
            CrawlJobIdResponse startResponse = crawlerClient.startZoneCrawlJob(state.crawlScope);
            state.crawlerJobId = startResponse.jobId();
         }
         state.currentPhase = "CRAWL_RUNNING";

         while (true) {
            ZoneCrawlStatusPayload crawlStatus = crawlerClient.getZoneCrawlJobStatus(state.crawlerJobId);
            state.applyCrawlerStatus(crawlStatus);

            state.currentPhase = "SAVE_RAW_STREAMING";
            state.nextResultIndex = fetchAndSaveIncrementalResults(state, state.nextResultIndex);

            if (crawlStatus.status() == ZoneCrawlJobStatus.CAPTCHA_REQUIRED) {
               state.markCaptchaRequired(crawlStatus);
               log.warn(
                       "구역 동기화 작업 보안 인증 대기. jobId={}, crawlerJobId={}, grid={}, preservedPlaceCount={}",
                       state.jobId,
                       state.crawlerJobId,
                       state.currentGrid,
                       state.discoveredPlaceCount
               );
               return;
            }

            if (crawlStatus.status() == ZoneCrawlJobStatus.SUCCESS) {
               state.currentPhase = "SAVE_RAW_FINALIZE";
               state.nextResultIndex = fetchAndSaveIncrementalResults(state, state.nextResultIndex);

               state.markSuccess();
               log.info(
                       "구역 동기화 작업 완료. jobId={}, scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, savedRawCount={}, saveFailedCount={}, lastSavedIndex={}",
                       state.jobId, state.crawlScope, state.discoveredPlaceCount, state.crawledSuccessCount,
                       state.savedRawCount, state.saveFailedCount, state.nextResultIndex
               );
               break;
            }

            if (crawlStatus.status() == ZoneCrawlJobStatus.FAILED) {
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

      private volatile ZoneCrawlJobStatus status;
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
      private volatile int nextResultIndex;
      private volatile String currentGrid;
      private volatile String currentPlaceId;
      private volatile String errorMessage;
      private volatile LocalDateTime startedAt;
      private volatile LocalDateTime finishedAt;

      private ZoneCrawlJobState(String jobId, ZoneType crawlScope) {
         this.jobId = jobId;
         this.crawlScope = crawlScope;
         this.status = ZoneCrawlJobStatus.PENDING;
      }

      private static ZoneCrawlJobState pending(String jobId, ZoneType crawlScope) {
         return new ZoneCrawlJobState(jobId, crawlScope);
      }

      private void markRunning() {
         this.status = ZoneCrawlJobStatus.RUNNING;
         this.currentPhase = "QUEUED";
         this.errorMessage = null;
         this.finishedAt = null;
         if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
         }
      }

      private synchronized void markResumeQueued() {
         if (status != ZoneCrawlJobStatus.CAPTCHA_REQUIRED) {
            throw new IllegalStateException(
                    "보안 인증 대기 상태의 작업만 재개할 수 있습니다. status=" + status
            );
         }
         if (crawlerJobId == null || crawlerJobId.isBlank()) {
            throw new IllegalStateException("재개할 크롤러 작업 ID가 없습니다.");
         }
         this.status = ZoneCrawlJobStatus.RUNNING;
         this.currentPhase = "RESUME_QUEUED";
         this.errorMessage = null;
         this.finishedAt = null;
      }

      private void markCaptchaRequired(ZoneCrawlStatusPayload crawlStatus) {
         this.status = ZoneCrawlJobStatus.CAPTCHA_REQUIRED;
         this.currentPhase = "CAPTCHA_REQUIRED";
         this.errorMessage = crawlStatus.errorMessage();
         this.finishedAt = null;
      }

      private void markSuccess() {
         this.status = ZoneCrawlJobStatus.SUCCESS;
         this.currentPhase = "COMPLETED";
         this.finishedAt = LocalDateTime.now();
      }

      private void markFailed(Exception e) {
         this.status = ZoneCrawlJobStatus.FAILED;
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
