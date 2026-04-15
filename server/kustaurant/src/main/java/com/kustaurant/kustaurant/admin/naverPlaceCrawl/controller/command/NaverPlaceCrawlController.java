package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command;

import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync.NaverPlaceZoneSyncJobStartResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync.NaverPlaceZoneSyncJobStatusResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync.NaverPlaceZoneSyncRequest;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync.NaverPlaceZoneSyncResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.NaverPlaceAnalyzeService;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.NaverPlaceRawSaveService;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.sync.NaverPlaceZoneSyncJobService;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.sync.NaverPlaceZoneSyncService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Hidden
@RestController
@RequestMapping({"/admin/api/crawl"})
public class NaverPlaceCrawlController {
   private final NaverPlaceRawSaveService naverPlaceRawSaveService;
   private final NaverPlaceAnalyzeService naverPlaceAnalyzeService;
   private final NaverPlaceZoneSyncService naverPlaceZoneSyncService;
   private final NaverPlaceZoneSyncJobService naverPlaceZoneSyncJobService;

   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @PostMapping({"/naver-place/raw"})
   public NaverPlaceRawCrawlResponse crawlNaverPlaceRaw(@RequestBody @Valid NaverPlaceRawCrawlRequest request) {
      return this.naverPlaceRawSaveService.crawlAndSave(request.placeUrl());
   }

   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @GetMapping({"/naver-place/raw/existence/{placeId}"})
   public NaverPlaceRawExistenceResponse getNaverPlaceRawExistence(@PathVariable String placeId) {
      return this.naverPlaceRawSaveService.findExistingByPlaceId(placeId)
              .map(entity -> new NaverPlaceRawExistenceResponse(
                      true,
                      entity.getSourcePlaceId(),
                      entity.getCrawlScope(),
                      entity.getCrawlScope() == null ? null : entity.getCrawlScope().getDescription(),
                      entity.getPlaceName()
              ))
              .orElseGet(() -> new NaverPlaceRawExistenceResponse(false, placeId, null, null, null));
   }

   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @PostMapping({"/naver-place/analyze"})
   public NaverPlaceRawCrawlResponse analyzeNaverPlaceRaw(@RequestBody @Valid NaverPlaceRawCrawlRequest request) {
      return this.naverPlaceAnalyzeService.analyze(request.placeUrl());
   }

   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @PostMapping({"/naver-place/sync-zone/test"})
   public NaverPlaceZoneSyncResponse syncNaverPlaceByZoneTest(@RequestBody @Valid NaverPlaceZoneSyncRequest request) {
      return this.naverPlaceZoneSyncService.syncByScopeTest(request.crawlScope());
   }

   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @PostMapping({"/naver-place/sync-zone/jobs"})
   public NaverPlaceZoneSyncJobStartResponse startNaverPlaceZoneSyncJob(@RequestBody @Valid NaverPlaceZoneSyncRequest request) {
      return this.naverPlaceZoneSyncJobService.start(request.crawlScope());
   }

   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @GetMapping({"/naver-place/sync-zone/jobs/{jobId}"})
   public NaverPlaceZoneSyncJobStatusResponse getNaverPlaceZoneSyncJobStatus(@PathVariable String jobId) {
      return this.naverPlaceZoneSyncJobService.getStatus(jobId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "zone sync job not found: " + jobId));
   }

   @Generated
   public NaverPlaceCrawlController(final NaverPlaceRawSaveService naverPlaceRawSaveService, final NaverPlaceAnalyzeService naverPlaceAnalyzeService, final NaverPlaceZoneSyncService naverPlaceZoneSyncService, final NaverPlaceZoneSyncJobService naverPlaceZoneSyncJobService) {
      this.naverPlaceRawSaveService = naverPlaceRawSaveService;
      this.naverPlaceAnalyzeService = naverPlaceAnalyzeService;
      this.naverPlaceZoneSyncService = naverPlaceZoneSyncService;
      this.naverPlaceZoneSyncJobService = naverPlaceZoneSyncJobService;
   }
}
