package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.restaurantSync.sync.ZoneCrawlJobResultsPayload;
import com.kustaurant.restaurantSync.sync.CrawlJobIdResponse;
import com.kustaurant.restaurantSync.sync.ZoneCrawlStatusPayload;
import com.kustaurant.restaurantSync.sync.ZoneCrawlResultPayload;
import com.kustaurant.restaurantSync.sync.ZoneCrawlJobStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

   private final ZoneCrawler zoneCrawler;
   private final Map<String, ZoneCrawlJobState> jobs = new ConcurrentHashMap<>();
   private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "naver-zone-crawl-worker");
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

      log.info("구역 크롤 작업 제출. jobId={}, scope={}", jobId, crawlScope);
      return new CrawlJobIdResponse(jobId);
   }

   public Optional<ZoneCrawlStatusPayload> getStatus(String jobId) {
      return Optional.ofNullable(jobs.get(jobId)).map(ZoneCrawlJobState::toStatusResponse);
   }

   public Optional<CrawlJobIdResponse> resume(String jobId) {
      ZoneCrawlJobState state = jobs.get(jobId);
      if (state == null) {
         return Optional.empty();
      }

      state.markResumeQueued();
      executor.submit(() -> runJob(state));
      log.info(
              "구역 크롤 작업 재개 제출. jobId={}, scope={}, nextGridIndex={}, preservedPlaceCount={}",
              jobId,
              state.crawlScope,
              state.checkpoint.nextGridIndex(),
              state.checkpoint.discoveredPlaceCount()
      );
      return Optional.of(new CrawlJobIdResponse(jobId));
   }

   public Optional<ZoneCrawlJobResultsPayload> getResults(String jobId, int fromIndex, int limit) {
      return Optional.ofNullable(jobs.get(jobId)).map(state -> state.toResultsResponse(fromIndex, limit));
   }

   private void runJob(ZoneCrawlJobState state) {
      state.markRunning();
      try {
         ZoneCrawlResultPayload result = zoneCrawler.crawlByScope(state.crawlScope, state.checkpoint, progress -> {
            state.currentPhase = progress.phase();
            state.totalGridCount = progress.totalGridCount();
            state.processedGridCount = progress.processedGridCount();
            state.discoveredPlaceCount = progress.discoveredPlaceCount();
            state.totalPlaceCount = progress.totalPlaceCount();
            state.attemptedPlaceCount = progress.attemptedPlaceCount();
            state.crawledSuccessCount = progress.crawledSuccessCount();
            state.finalFailedCount = progress.finalFailedCount();
            state.finalFailedPlaceIds = progress.finalFailedPlaceIds();
            state.currentGrid = progress.currentGrid();
            state.currentPlaceId = progress.currentPlaceId();

            if (progress.acceptedResult() != null) state.acceptedResults.add(progress.acceptedResult());
         });

         state.markSuccess(result);
         log.info(
                 "구역 크롤 작업 종료. jobId={}, scope={}, discoveredPlaceCount={}, crawledSuccessCount={}",
                 state.jobId,
                 state.crawlScope,
                 result.discoveredPlaceCount(),
                 result.successCount()
         );
      } catch (NaverCaptchaRequiredException e) {
         state.markCaptchaRequired(e);
         log.warn(
                 "구역 크롤 작업 보안 인증 대기. jobId={}, scope={}, grid={}, preservedGridCount={}, preservedPlaceCount={}",
                 state.jobId,
                 state.crawlScope,
                 e.grid(),
                 state.checkpoint.nextGridIndex(),
                 state.checkpoint.discoveredPlaceCount()
         );
      } catch (Exception e) {
         state.markFailed(e);
         log.warn(
                 "구역 크롤 작업 실패. jobId={}, scope={}, message={}",
                 state.jobId,
                 state.crawlScope,
                 e.getMessage(),
                 e
         );
      }
   }

   private static final class ZoneCrawlJobState {
      private final String jobId;
      private final ZoneType crawlScope;
      private final ZoneCrawlCheckpoint checkpoint = new ZoneCrawlCheckpoint();

      private volatile ZoneCrawlJobStatus status;
      private volatile String currentPhase;
      private volatile int totalGridCount;
      private volatile int processedGridCount;
      private volatile int discoveredPlaceCount;
      private volatile int totalPlaceCount;
      private volatile int attemptedPlaceCount;
      private volatile int crawledSuccessCount;
      private volatile int finalFailedCount;
      private volatile List<String> finalFailedPlaceIds = List.of();
      private volatile String currentGrid;
      private volatile String currentPlaceId;
      private volatile String errorMessage;
      private volatile LocalDateTime startedAt;
      private volatile LocalDateTime finishedAt;
      private volatile ZoneCrawlResultPayload result;
      private final List<RestaurantRaw> acceptedResults = Collections.synchronizedList(new ArrayList<>());

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
         this.currentPhase = "CRAWL_START";
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
         this.status = ZoneCrawlJobStatus.RUNNING;
         this.currentPhase = "RESUME_QUEUED";
         this.errorMessage = null;
         this.finishedAt = null;
      }

      private void markCaptchaRequired(NaverCaptchaRequiredException e) {
         this.status = ZoneCrawlJobStatus.CAPTCHA_REQUIRED;
         this.currentPhase = "CAPTCHA_REQUIRED";
         this.currentGrid = e.grid();
         this.errorMessage = e.getMessage();
         this.finishedAt = null;
      }

      private void markSuccess(ZoneCrawlResultPayload result) {
         this.result = result;
         this.status = ZoneCrawlJobStatus.SUCCESS;
         this.currentPhase = "COMPLETED";
         this.finishedAt = LocalDateTime.now();
      }

      private void markFailed(Exception e) {
         this.status = ZoneCrawlJobStatus.FAILED;
         this.errorMessage = e.getMessage();
         this.finishedAt = LocalDateTime.now();
      }

      private ZoneCrawlStatusPayload toStatusResponse() {
         return new ZoneCrawlStatusPayload(
                 status,
                 totalGridCount,
                 processedGridCount,
                 discoveredPlaceCount,
                 attemptedPlaceCount,
                 crawledSuccessCount,
                 finalFailedCount,
                 finalFailedPlaceIds,
                 currentGrid,
                 currentPlaceId,
                 errorMessage
         );
      }

      private ZoneCrawlJobResultsPayload toResultsResponse(int fromIndex, int limit) {
         int safeFromIndex = Math.max(0, fromIndex);
         int safeLimit = limit <= 0 ? 50 : Math.min(limit, 500);

         synchronized (acceptedResults) {
            int total = acceptedResults.size();
            int start = Math.min(safeFromIndex, total);
            int end = Math.min(start + safeLimit, total);
            List<RestaurantRaw> chunk = List.copyOf(acceptedResults.subList(start, end));
            return new ZoneCrawlJobResultsPayload(
                    end,
                    total,
                    chunk
            );
         }
      }
   }
}
