package com.kustaurant.crawler.RestaurantSync.controller;

import com.kustaurant.crawler.RestaurantSync.service.single.NaverPlaceAnalyzeCrawler;
import com.kustaurant.crawler.RestaurantSync.service.single.NaverPlaceCrawler;
import com.kustaurant.crawler.RestaurantSync.service.zone.NaverPlaceZoneCrawlJobService;
import com.kustaurant.crawler.RestaurantSync.service.zone.NaverPlaceZoneTestCrawler;
import com.kustaurant.crawler.RestaurantSync.service.zone.NaverPlaceZoneTestCrawler2;
import com.kustaurant.naverplace.NaverPlaceCrawlRequest;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobResultResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStartResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStatusResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlRequest;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlResult;
import lombok.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping({"/api/naver-place"})
public class NaverPlaceCrawlController {
   private final NaverPlaceCrawler crawler;
   private final NaverPlaceAnalyzeCrawler analyzeCrawler;
   private final NaverPlaceZoneTestCrawler test;
   private final NaverPlaceZoneTestCrawler2 test2;
   private final NaverPlaceZoneCrawlJobService zoneCrawlJobService;

   @PostMapping({"/crawl-one"})
   public NaverPlaceCrawlResult crawlOne(@RequestBody NaverPlaceCrawlRequest request) {
      return this.crawler.crawl(request.placeUrl());
   }

   @PostMapping({"/analyze-one"})
   public NaverPlaceCrawlResult analyzeOne(@RequestBody NaverPlaceCrawlRequest request) {
      return this.analyzeCrawler.analyze(request.placeUrl());
   }

   @PostMapping({"/crawl-zone/test"})
   public NaverPlaceZoneCrawlResult crawlZone(@RequestBody NaverPlaceZoneCrawlRequest request) {
      return this.test.test(request.crawlScope());
   }

   @PostMapping({"/crawl-zone/jobs"})
   public NaverPlaceZoneCrawlJobStartResponse startZoneCrawlJob(@RequestBody NaverPlaceZoneCrawlRequest request) {
      return this.zoneCrawlJobService.start(request.crawlScope());
   }

   @GetMapping({"/crawl-zone/jobs/{jobId}"})
   public NaverPlaceZoneCrawlJobStatusResponse getZoneCrawlJobStatus(@PathVariable String jobId) {
      return (NaverPlaceZoneCrawlJobStatusResponse)this.zoneCrawlJobService.getStatus(jobId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "zone crawl job not found: " + jobId));
   }

   @GetMapping({"/crawl-zone/jobs/{jobId}/result"})
   public NaverPlaceZoneCrawlJobResultResponse getZoneCrawlJobResult(@PathVariable String jobId) {
      return (NaverPlaceZoneCrawlJobResultResponse)this.zoneCrawlJobService.getResult(jobId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "zone crawl job not found: " + jobId));
   }

   @Generated
   public NaverPlaceCrawlController(final NaverPlaceCrawler crawler, final NaverPlaceAnalyzeCrawler analyzeCrawler, final NaverPlaceZoneTestCrawler test, final NaverPlaceZoneTestCrawler2 test2, final NaverPlaceZoneCrawlJobService zoneCrawlJobService) {
      this.crawler = crawler;
      this.analyzeCrawler = analyzeCrawler;
      this.test = test;
      this.test2 = test2;
      this.zoneCrawlJobService = zoneCrawlJobService;
   }
}
