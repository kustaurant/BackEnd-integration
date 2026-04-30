package com.kustaurant.crawler.RestaurantSync.controller;

import com.kustaurant.crawler.RestaurantSync.service.single.RestaurantSingleCrawler;
import com.kustaurant.crawler.RestaurantSync.service.zone.ZoneCrawlJobService;
import com.kustaurant.crawler.RestaurantSync.service.zone.ZoneTestCrawler;
import com.kustaurant.restaurantSync.RestaurantCrawlRequest;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.restaurantSync.sync.ZoneCrawlJobResultsPayload;
import com.kustaurant.restaurantSync.sync.CrawlJobIdResponse;
import com.kustaurant.restaurantSync.sync.ZoneCrawlStatusPayload;
import com.kustaurant.restaurantSync.sync.ZoneCrawlRequest;
import com.kustaurant.restaurantSync.sync.ZoneCrawlResultPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/naver-place"})
public class RestaurantCrawlController {
   private final RestaurantSingleCrawler crawler;
   private final ZoneTestCrawler testCrawler;
   private final ZoneCrawlJobService zoneCrawlJobService;

   // 1. 단건 크롤 & 저장
   @PostMapping({"/crawl-one"})
   public RestaurantRaw crawlOne(@RequestBody @Valid RestaurantCrawlRequest request) {
      return crawler.crawl(request.normalizedPlaceUrl());
   }
   // 2. 단건 분석 (저장x, 로그만 남음)
   @PostMapping({"/analyze-one"})
   public RestaurantRaw analyzeOne(@RequestBody @Valid RestaurantCrawlRequest request) {
      return crawler.analyze(request.normalizedPlaceUrl());
   }

   // 3. 구역 크롤 테스트 (저장x, 로그만 남음)
   @PostMapping({"/crawl-zone/test"})
   public ZoneCrawlResultPayload crawlZone(@RequestBody ZoneCrawlRequest request) {
      return testCrawler.testCrawl(request.crawlScope());
   }
   // 4. 구역 크롤 작업 시작
   @PostMapping({"/crawl-zone/jobs"})
   public CrawlJobIdResponse startZoneCrawlJob(@RequestBody ZoneCrawlRequest request) {
      return zoneCrawlJobService.start(request.crawlScope());
   }

   // 5. 구역 크롤 작업 상태 조회
   @GetMapping({"/crawl-zone/jobs/{jobId}"})
   public ZoneCrawlStatusPayload getZoneCrawlJobStatus(@PathVariable String jobId) {
      return zoneCrawlJobService.getStatus(jobId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "zone crawl job not found: " + jobId));
   }
   // 6. 증분 결과 조회 (구역 크롤 실시간 배치 저장용 (현재 10개씩))
   @GetMapping({"/crawl-zone/jobs/{jobId}/results"})
   public ZoneCrawlJobResultsPayload getZoneCrawlJobResults(
           @PathVariable String jobId,
           @RequestParam(defaultValue = "0") int fromIndex,
           @RequestParam(defaultValue = "50") int limit
   ) {
      return this.zoneCrawlJobService
              .getResults(jobId, fromIndex, limit)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "zone crawl job not found: " + jobId));
   }
}
