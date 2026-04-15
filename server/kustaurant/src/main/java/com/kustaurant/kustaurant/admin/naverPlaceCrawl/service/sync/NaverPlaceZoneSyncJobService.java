package com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.sync;

import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync.NaverPlaceZoneSyncJobStartResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync.NaverPlaceZoneSyncJobStatusResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.NaverPlaceRawSaveService;
import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobResultResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStartResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStatusResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlResult;
import com.kustaurant.naverplace.sync.NaverPlaceZoneJobStatus;
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
public class NaverPlaceZoneSyncJobService {

   private final RestaurantCrawlerClient crawlerClient;
   private final NaverPlaceRawSaveService rawSaveService;

   private final Map<String, ZoneSyncJobState> jobs = new ConcurrentHashMap<>();
   private final ExecutorService executor = Executors.newCachedThreadPool();

   public NaverPlaceZoneSyncJobStartResponse start(CrawlScopeType crawlScope) {
      String jobId = UUID.randomUUID().toString();
      ZoneSyncJobState state = ZoneSyncJobState.pending(jobId, crawlScope);
      jobs.put(jobId, state);
      executor.submit(() -> runJob(state));

      log.info("구역 동기화 작업 등록. jobId={}, scope={}", jobId, crawlScope);
      return new NaverPlaceZoneSyncJobStartResponse(jobId, crawlScope, state.status);
   }

   public Optional<NaverPlaceZoneSyncJobStatusResponse> getStatus(String jobId) {
      return Optional.ofNullable(jobs.get(jobId)).map(ZoneSyncJobState::toResponse);
   }

   private void runJob(ZoneSyncJobState state) {
      state.markRunning();
      try {
         state.currentPhase = "CRAWL_JOB_START";
         NaverPlaceZoneCrawlJobStartResponse startResponse = crawlerClient.startZoneCrawlJob(state.crawlScope);
         state.crawlerJobId = startResponse.jobId();
         state.currentPhase = "CRAWL_RUNNING";

         while (true) {
            NaverPlaceZoneCrawlJobStatusResponse crawlStatus = crawlerClient.getZoneCrawlJobStatus(state.crawlerJobId);
            state.applyCrawlerStatus(crawlStatus);

            if (crawlStatus.status() == NaverPlaceZoneJobStatus.SUCCESS) {
               state.currentPhase = "FETCH_CRAWL_RESULT";
               NaverPlaceZoneCrawlJobResultResponse jobResult = crawlerClient.getZoneCrawlJobResult(state.crawlerJobId);
               NaverPlaceZoneCrawlResult zoneResult = jobResult.result();
               if (zoneResult == null) {
                  throw new IllegalStateException("크롤러 작업 결과가 비어있습니다.");
               }

               state.currentPhase = "SAVE_RAW";
               List<NaverPlaceCrawlResult> crawlResults =
                       zoneResult.results() == null ? List.of() : zoneResult.results();
               for (NaverPlaceCrawlResult result : crawlResults) {
                  state.currentPlaceId = result == null ? null : result.sourcePlaceId();
                  try {
                     rawSaveService.saveResult(result, state.crawlScope);
                     state.savedRawCount++;
                  } catch (Exception e) {
                     state.saveFailedCount++;
                     log.warn(
                             "구역 동기화 raw 저장 실패. jobId={}, scope={}, placeId={}",
                             state.jobId,
                             state.crawlScope,
                             state.currentPlaceId,
                             e
                     );
                  }
               }

               state.markSuccess();
               log.info(
                       "구역 동기화 작업 완료. jobId={}, scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, savedRawCount={}, saveFailedCount={}",
                       state.jobId,
                       state.crawlScope,
                       state.discoveredPlaceCount,
                       state.crawledSuccessCount,
                       state.savedRawCount,
                       state.saveFailedCount
               );
               break;
            }

            if (crawlStatus.status() == NaverPlaceZoneJobStatus.FAILED) {
               throw new IllegalStateException("크롤러 작업 실패: " + crawlStatus.errorMessage());
            }

            sleep(1500);
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

   private void sleep(long millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   private static final class ZoneSyncJobState {
      private final String jobId;
      private final CrawlScopeType crawlScope;

      private volatile NaverPlaceZoneJobStatus status;
      private volatile String crawlerJobId;
      private volatile String currentPhase;
      private volatile int totalGridCount;
      private volatile int processedGridCount;
      private volatile int discoveredPlaceCount;
      private volatile int totalPlaceCount;
      private volatile int attemptedPlaceCount;
      private volatile int crawledSuccessCount;
      private volatile int savedRawCount;
      private volatile int saveFailedCount;
      private volatile String currentGrid;
      private volatile String currentPlaceId;
      private volatile String errorMessage;
      private volatile LocalDateTime startedAt;
      private volatile LocalDateTime finishedAt;

      private ZoneSyncJobState(String jobId, CrawlScopeType crawlScope) {
         this.jobId = jobId;
         this.crawlScope = crawlScope;
         this.status = NaverPlaceZoneJobStatus.PENDING;
      }

      private static ZoneSyncJobState pending(String jobId, CrawlScopeType crawlScope) {
         return new ZoneSyncJobState(jobId, crawlScope);
      }

      private void markRunning() {
         this.status = NaverPlaceZoneJobStatus.RUNNING;
         this.currentPhase = "QUEUED";
         this.startedAt = LocalDateTime.now();
      }

      private void markSuccess() {
         this.status = NaverPlaceZoneJobStatus.SUCCESS;
         this.currentPhase = "COMPLETED";
         this.finishedAt = LocalDateTime.now();
      }

      private void markFailed(Exception e) {
         this.status = NaverPlaceZoneJobStatus.FAILED;
         this.errorMessage = e.getMessage();
         this.finishedAt = LocalDateTime.now();
      }

      private void applyCrawlerStatus(NaverPlaceZoneCrawlJobStatusResponse crawlStatus) {
         this.totalGridCount = crawlStatus.totalGridCount();
         this.processedGridCount = crawlStatus.processedGridCount();
         this.discoveredPlaceCount = crawlStatus.discoveredPlaceCount();
         this.totalPlaceCount = crawlStatus.totalPlaceCount();
         this.attemptedPlaceCount = crawlStatus.attemptedPlaceCount();
         this.crawledSuccessCount = crawlStatus.crawledSuccessCount();
         this.currentGrid = crawlStatus.currentGrid();
         this.currentPlaceId = crawlStatus.currentPlaceId();
      }

      private NaverPlaceZoneSyncJobStatusResponse toResponse() {
         return new NaverPlaceZoneSyncJobStatusResponse(
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
                 savedRawCount,
                 saveFailedCount,
                 currentGrid,
                 currentPlaceId,
                 errorMessage,
                 startedAt,
                 finishedAt
         );
      }
   }
}
