package com.kustaurant.crawler.RestaurantSync.service.zone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.sync.ZoneCrawlResultPayload;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ZoneCrawlJobServiceConcurrencyTest {

   @Test
   void runsOnlyOneZoneCrawlAtATime() throws Exception {
      ZoneCrawler zoneCrawler = mock(ZoneCrawler.class);
      AtomicInteger active = new AtomicInteger();
      AtomicInteger maxActive = new AtomicInteger();
      CountDownLatch completed = new CountDownLatch(2);

      when(zoneCrawler.crawlByScope(any(), any(), any())).thenAnswer(invocation -> {
         int current = active.incrementAndGet();
         maxActive.accumulateAndGet(current, Math::max);
         try {
            Thread.sleep(100);
            return new ZoneCrawlResultPayload(0, 0, List.of());
         } finally {
            active.decrementAndGet();
            completed.countDown();
         }
      });

      ZoneCrawlJobService service = new ZoneCrawlJobService(zoneCrawler);
      try {
         service.start(ZoneType.GUI_STATION);
         service.start(ZoneType.BACK_GATE);

         assertThat(completed.await(5, TimeUnit.SECONDS)).isTrue();
         assertThat(maxActive).hasValue(1);
      } finally {
         service.shutdownExecutor();
      }
   }
}
