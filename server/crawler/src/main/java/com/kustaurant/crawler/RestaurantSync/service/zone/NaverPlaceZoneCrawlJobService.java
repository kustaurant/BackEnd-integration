package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobResultResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStartResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStatusResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlResult;
import com.kustaurant.naverplace.sync.NaverPlaceZoneJobStatus;
import java.time.LocalDateTime;
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
public class NaverPlaceZoneCrawlJobService {

   private final NaverPlaceZoneCrawler zoneCrawler;
   private final Map<String, ZoneCrawlJobState> jobs = new ConcurrentHashMap<>();
   private final ExecutorService executor = Executors.newCachedThreadPool();

   public NaverPlaceZoneCrawlJobStartResponse start(CrawlScopeType crawlScope) {
      String jobId = UUID.randomUUID().toString();
      ZoneCrawlJobState state = ZoneCrawlJobState.pending(jobId, crawlScope);
      jobs.put(jobId, state);
      executor.submit(() -> runJob(state));

      log.info("zone crawl job submitted. jobId={}, scope={}", jobId, crawlScope);
      return new NaverPlaceZoneCrawlJobStartResponse(jobId, crawlScope, state.status);
   }

   public Optional<NaverPlaceZoneCrawlJobStatusResponse> getStatus(String jobId) {
      return Optional.ofNullable(jobs.get(jobId)).map(ZoneCrawlJobState::toStatusResponse);
   }

   public Optional<NaverPlaceZoneCrawlJobResultResponse> getResult(String jobId) {
      return Optional.ofNullable(jobs.get(jobId)).map(ZoneCrawlJobState::toResultResponse);
   }

   private void runJob(ZoneCrawlJobState state) {
      state.markRunning();
      try {
         NaverPlaceZoneCrawlResult result = zoneCrawler.crawlByScope(state.crawlScope, progress -> {
            state.currentPhase = progress.phase();
            state.totalGridCount = progress.totalGridCount();
            state.processedGridCount = progress.processedGridCount();
            state.discoveredPlaceCount = progress.discoveredPlaceCount();
            state.totalPlaceCount = progress.totalPlaceCount();
            state.attemptedPlaceCount = progress.attemptedPlaceCount();
            state.crawledSuccessCount = progress.crawledSuccessCount();
            state.currentGrid = progress.currentGrid();
            state.currentPlaceId = progress.currentPlaceId();
         });

         state.markSuccess(result);
         log.info(
                 "zone crawl job finished. jobId={}, scope={}, discoveredPlaceCount={}, crawledSuccessCount={}",
                 state.jobId,
                 state.crawlScope,
                 result.discoveredPlaceCount(),
                 result.successCount()
         );
      } catch (Exception e) {
         state.markFailed(e);
         log.warn(
                 "zone crawl job failed. jobId={}, scope={}, message={}",
                 state.jobId,
                 state.crawlScope,
                 e.getMessage(),
                 e
         );
      }
   }

   private static final class ZoneCrawlJobState {
      private final String jobId;
      private final CrawlScopeType crawlScope;

      private volatile NaverPlaceZoneJobStatus status;
      private volatile String currentPhase;
      private volatile int totalGridCount;
      private volatile int processedGridCount;
      private volatile int discoveredPlaceCount;
      private volatile int totalPlaceCount;
      private volatile int attemptedPlaceCount;
      private volatile int crawledSuccessCount;
      private volatile String currentGrid;
      private volatile String currentPlaceId;
      private volatile String errorMessage;
      private volatile LocalDateTime startedAt;
      private volatile LocalDateTime finishedAt;
      private volatile NaverPlaceZoneCrawlResult result;

      private ZoneCrawlJobState(String jobId, CrawlScopeType crawlScope) {
         this.jobId = jobId;
         this.crawlScope = crawlScope;
         this.status = NaverPlaceZoneJobStatus.PENDING;
      }

      private static ZoneCrawlJobState pending(String jobId, CrawlScopeType crawlScope) {
         return new ZoneCrawlJobState(jobId, crawlScope);
      }

      private void markRunning() {
         this.status = NaverPlaceZoneJobStatus.RUNNING;
         this.currentPhase = "CRAWL_START";
         this.startedAt = LocalDateTime.now();
      }

      private void markSuccess(NaverPlaceZoneCrawlResult result) {
         this.result = result;
         this.status = NaverPlaceZoneJobStatus.SUCCESS;
         this.currentPhase = "COMPLETED";
         this.finishedAt = LocalDateTime.now();
      }

      private void markFailed(Exception e) {
         this.status = NaverPlaceZoneJobStatus.FAILED;
         this.errorMessage = e.getMessage();
         this.finishedAt = LocalDateTime.now();
      }

      private NaverPlaceZoneCrawlJobStatusResponse toStatusResponse() {
         return new NaverPlaceZoneCrawlJobStatusResponse(
                 jobId,
                 crawlScope,
                 status,
                 currentPhase,
                 totalGridCount,
                 processedGridCount,
                 discoveredPlaceCount,
                 totalPlaceCount,
                 attemptedPlaceCount,
                 crawledSuccessCount,
                 currentGrid,
                 currentPlaceId,
                 errorMessage,
                 startedAt,
                 finishedAt
         );
      }

      private NaverPlaceZoneCrawlJobResultResponse toResultResponse() {
         return new NaverPlaceZoneCrawlJobResultResponse(jobId, crawlScope, status, result);
      }
   }
}
