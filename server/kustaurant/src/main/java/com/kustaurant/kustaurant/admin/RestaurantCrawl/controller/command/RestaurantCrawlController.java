package com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.command;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.dto.ZoneCrawlJobStatusResponse;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.dto.ZoneCrawlResultResponse;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.service.RestaurantAnalyzeService;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.service.RestaurantRawQueryService;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.service.RestaurantRawSaveService;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.service.ZoneCrawlJobService;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.service.ZoneCrawlTestService;
import com.kustaurant.restaurantSync.RestaurantCrawlRequest;
import com.kustaurant.restaurantSync.sync.CrawlJobIdResponse;
import com.kustaurant.restaurantSync.sync.ZoneCrawlRequest;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping({"/admin/api/crawl"})
public class RestaurantCrawlController {
   private final RestaurantRawSaveService restaurantRawSaveService;
   private final RestaurantRawQueryService restaurantRawQueryService;
   private final RestaurantAnalyzeService restaurantAnalyzeService;
   private final ZoneCrawlTestService zoneCrawlTestService;
   private final ZoneCrawlJobService zoneCrawlJobService;

   // 1. 단건 크롤 수행 ( db 저장)
   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @PostMapping({"/naver-place/raw"})
   public RestaurantCrawlResponse crawlNaverPlaceRaw(@RequestBody @Valid RestaurantCrawlRequest request) {
      return this.restaurantRawSaveService.crawlAndSave(request.placeId());
   }

   // 2. 해당 placeId를 가진 식당이 raw DB에 있는지 조회
   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @GetMapping({"/naver-place/raw/existence/{placeId}"})
   public RestaurantRawExistenceResponse getNaverPlaceRawExistence(@PathVariable String placeId) {
      return this.restaurantRawQueryService.getRawExistence(placeId);
   }

   // 3. 단건 크롤 분석 ( db 저장x 응답 확인용)
   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @PostMapping({"/naver-place/analyze"})
   public RestaurantCrawlResponse analyzeNaverPlaceRaw(
           @RequestBody @Valid RestaurantCrawlRequest request
   ) {
      return restaurantAnalyzeService.analyze(request.placeId());
   }

   // 4. 구역 크롤 테스트 실행 ( db 저장 x)
   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @PostMapping({"/naver-place/crawl-zone/test"})
   public ZoneCrawlResultResponse crawlNaverPlaceByZoneTest(
           @RequestBody @Valid ZoneCrawlRequest request
   ) {
      return zoneCrawlTestService.crawlByScopeTest(request.crawlScope());
   }

   // 5. 구역 크롤 비동기 job 시작
   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @PostMapping({"/naver-place/crawl-zone/jobs"})
   public CrawlJobIdResponse startNaverPlaceZoneCrawlJob(
           @RequestBody @Valid ZoneCrawlRequest request
   ) {
      return zoneCrawlJobService.start(request.crawlScope());
   }

   // 6. 구역 크롤 해당 job 진행상태 조회
   @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
   @GetMapping({"/naver-place/crawl-zone/jobs/{jobId}"})
   public ZoneCrawlJobStatusResponse getNaverPlaceZoneCrawlJobStatus(
           @PathVariable String jobId
   ) {
      return zoneCrawlJobService.getStatus(jobId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "zone crawl job not found: " + jobId));
   }
}
