package com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.sync;

import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync.NaverPlaceZoneSyncJobStartResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.NaverPlaceRawSaveService;
import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobResultResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStartResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStatusResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlResult;
import com.kustaurant.naverplace.sync.NaverPlaceZoneJobStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NaverPlaceZoneSyncJobService {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(NaverPlaceZoneSyncJobService.class);
   private final RestaurantCrawlerClient crawlerClient;
   private final NaverPlaceRawSaveService rawSaveService;
   private final Map jobs = new ConcurrentHashMap();
   private final ExecutorService executor = Executors.newCachedThreadPool();

   public NaverPlaceZoneSyncJobStartResponse start(CrawlScopeType crawlScope) {
      String jobId = UUID.randomUUID().toString();
      ZoneSyncJobState state = com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.sync.NaverPlaceZoneSyncJobService.ZoneSyncJobState.pending(jobId, crawlScope);
      this.jobs.put(jobId, state);
      this.executor.submit(() -> this.runJob(state));
      log.info("zone sync job submitted. jobId={}, scope={}", jobId, crawlScope);
      return new NaverPlaceZoneSyncJobStartResponse(jobId, crawlScope, state.status);
   }

   public Optional getStatus(String jobId) {
      return Optional.ofNullable((ZoneSyncJobState)this.jobs.get(jobId)).map(ZoneSyncJobState::toResponse);
   }

   private void runJob(ZoneSyncJobState state) {
      state.markRunning();

      try {
         state.currentPhase = "CRAWL_JOB_START";
         NaverPlaceZoneCrawlJobStartResponse startResponse = this.crawlerClient.startZoneCrawlJob(state.crawlScope);
         state.crawlerJobId = startResponse.jobId();
         state.currentPhase = "CRAWL_RUNNING";

         while(true) {
            NaverPlaceZoneCrawlJobStatusResponse crawlStatus = this.crawlerClient.getZoneCrawlJobStatus(state.crawlerJobId);
            state.applyCrawlerStatus(crawlStatus);
            if (crawlStatus.status() == NaverPlaceZoneJobStatus.SUCCESS) {
               state.currentPhase = "FETCH_CRAWL_RESULT";
               NaverPlaceZoneCrawlJobResultResponse jobResult = this.crawlerClient.getZoneCrawlJobResult(state.crawlerJobId);
               NaverPlaceZoneCrawlResult zoneResult = jobResult.result();
               if (zoneResult == null) {
                  throw new IllegalStateException("crawler job result is empty");
               }

               state.currentPhase = "SAVE_RAW";

               for(NaverPlaceCrawlResult result : zoneResult.results() == null ? List.of() : zoneResult.results()) {
                  state.currentPlaceId = result == null ? null : result.sourcePlaceId();

                  try {
                     this.rawSaveService.saveResult(result, state.crawlScope);
                     ++state.savedRawCount;
                  } catch (Exception e) {
                     ++state.saveFailedCount;
                     log.warn("zone sync raw save failed. jobId={}, scope={}, placeId={}", new Object[]{state.jobId, state.crawlScope, state.currentPlaceId, e});
                  }
               }

               state.markSuccess();
               log.info("zone sync job finished. jobId={}, scope={}, discoveredPlaceCount={}, crawledSuccessCount={}, savedRawCount={}, saveFailedCount={}", new Object[]{state.jobId, state.crawlScope, state.discoveredPlaceCount, state.crawledSuccessCount, state.savedRawCount, state.saveFailedCount});
               break;
            }

            if (crawlStatus.status() == NaverPlaceZoneJobStatus.FAILED) {
               throw new IllegalStateException("crawler job failed: " + crawlStatus.errorMessage());
            }

            this.sleep(1500L);
         }
      } catch (Exception e) {
         state.markFailed(e);
         log.warn("zone sync job failed. jobId={}, scope={}, message={}", new Object[]{state.jobId, state.crawlScope, e.getMessage(), e});
      }

   }

   private void sleep(long millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException var4) {
         Thread.currentThread().interrupt();
      }

   }

   @Generated
   public NaverPlaceZoneSyncJobService(final RestaurantCrawlerClient crawlerClient, final NaverPlaceRawSaveService rawSaveService) {
      this.crawlerClient = crawlerClient;
      this.rawSaveService = rawSaveService;
   }
}
