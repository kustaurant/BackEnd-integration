package com.kustaurant.crawler.RestaurantSync.service.zone;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kustaurant.crawler.RestaurantSync.CrawlGrid;
import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.kustaurant.map.ZoneType;
import org.junit.jupiter.api.Test;

class ZonePlaceIdCollectorFailureTest {

   @Test
   void pausesImmediatelyOnTheFirstCaptchaWithoutAutomaticRetry() {
      PlaywrightManager playwrightManager = mock(PlaywrightManager.class);
      ZonePlaceIdCollector collector = new ZonePlaceIdCollector(
              playwrightManager,
              mock(NaverCaptchaDetector.class)
      );
      when(playwrightManager.crawl(any())).thenThrow(new NaverCaptchaRequiredException(1, 2));

      assertThatThrownBy(() -> collector.discoverPlaceIdsFromGrid(grid(1, 2), 19))
              .isInstanceOf(NaverCaptchaRequiredException.class)
              .hasMessageContaining("grid=1,2");
      verify(playwrightManager, times(1)).crawl(any());
   }

   @Test
   void throwsAfterRetriesInsteadOfReturningAnEmptySuccessfulGrid() {
      PlaywrightManager playwrightManager = mock(PlaywrightManager.class);
      ZonePlaceIdCollector collector = new ZonePlaceIdCollector(
              playwrightManager,
              mock(NaverCaptchaDetector.class)
      );
      when(playwrightManager.crawl(any())).thenThrow(new IllegalStateException("food category button not found"));

      Thread.currentThread().interrupt();
      try {
         assertThatThrownBy(() -> collector.discoverPlaceIdsFromGrid(grid(0, 0), 19))
                 .isInstanceOf(NaverGridDiscoveryException.class)
                 .hasMessageContaining("grid=0,0");
      } finally {
         Thread.interrupted();
      }
      verify(playwrightManager, times(3)).crawl(any());
   }

   private CrawlGrid grid(int row, int col) {
      return new CrawlGrid(ZoneType.GUI_STATION, row, col, 37.5, 127.0);
   }
}
