package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStartResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlResult;
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
public class NaverPlaceZoneCrawlJobService {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(NaverPlaceZoneCrawlJobService.class);
   private final NaverPlaceZoneCrawler zoneCrawler;
   private final Map jobs = new ConcurrentHashMap();
   private final ExecutorService executor = Executors.newCachedThreadPool();

   public NaverPlaceZoneCrawlJobStartResponse start(CrawlScopeType crawlScope) {
      String jobId = UUID.randomUUID().toString();
      ZoneCrawlJobState state = com.kustaurant.crawler.RestaurantSync.service.zone.NaverPlaceZoneCrawlJobService.ZoneCrawlJobState.pending(jobId, crawlScope);
      this.jobs.put(jobId, state);
      this.executor.submit(() -> this.runJob(state));
      log.info("zone crawl job submitted. jobId={}, scope={}", jobId, crawlScope);
      return new NaverPlaceZoneCrawlJobStartResponse(jobId, crawlScope, state.status);
   }

   public Optional getStatus(String jobId) {
      return Optional.ofNullable((ZoneCrawlJobState)this.jobs.get(jobId)).map(ZoneCrawlJobState::toStatusResponse);
   }

   public Optional getResult(String jobId) {
      return Optional.ofNullable((ZoneCrawlJobState)this.jobs.get(jobId)).map(ZoneCrawlJobState::toResultResponse);
   }

   private void runJob(ZoneCrawlJobState state) {
      state.markRunning();

      try {
         NaverPlaceZoneCrawlResult result = this.zoneCrawler.crawlByScope(state.crawlScope, (progress) -> {
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
         log.info("zone crawl job finished. jobId={}, scope={}, discoveredPlaceCount={}, crawledSuccessCount={}", new Object[]{state.jobId, state.crawlScope, result.discoveredPlaceCount(), result.successCount()});
      } catch (Exception e) {
         state.markFailed(e);
         log.warn("zone crawl job failed. jobId={}, scope={}, message={}", new Object[]{state.jobId, state.crawlScope, e.getMessage(), e});
      }

   }

   @Generated
   public NaverPlaceZoneCrawlJobService(final NaverPlaceZoneCrawler zoneCrawler) {
      this.zoneCrawler = zoneCrawler;
   }
}
