package com.kustaurant.crawler.RestaurantSync.service.single;

import lombok.Generated;
import org.springframework.stereotype.Component;

@Component
public class NaverPlaceAnalyzeCrawler {
   private final NaverPlaceCrawler crawler;

   public RestaurantCrawlResponse analyze(String placeUrl) {
      return this.crawler.analyze(placeUrl);
   }

   @Generated
   public NaverPlaceAnalyzeCrawler(final NaverPlaceCrawler crawler) {
      this.crawler = crawler;
   }
}
