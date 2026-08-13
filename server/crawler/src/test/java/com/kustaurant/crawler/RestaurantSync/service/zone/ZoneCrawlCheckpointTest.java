package com.kustaurant.crawler.RestaurantSync.service.zone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.Test;

class ZoneCrawlCheckpointTest {

   @Test
   void preservesOnlyCompletedGridsAndResumesAtNextGrid() {
      ZoneCrawlCheckpoint checkpoint = new ZoneCrawlCheckpoint();

      checkpoint.completeGrid(0, Set.of("101", "102"), 100);

      assertThat(checkpoint.nextGridIndex()).isEqualTo(1);
      assertThat(checkpoint.discoveredPlaceIdsSnapshot()).containsExactlyInAnyOrder("101", "102");

      // 두 번째 그리드에서 캡차가 발생하면 completeGrid를 호출하지 않으므로 체크포인트는 그대로다.
      assertThat(checkpoint.nextGridIndex()).isEqualTo(1);
      assertThat(checkpoint.discoveredPlaceCount()).isEqualTo(2);

      checkpoint.completeGrid(1, Set.of("102", "103"), 100);

      assertThat(checkpoint.nextGridIndex()).isEqualTo(2);
      assertThat(checkpoint.discoveredPlaceIdsSnapshot()).containsExactlyInAnyOrder("101", "102", "103");
   }

   @Test
   void rejectsOutOfOrderGridCompletion() {
      ZoneCrawlCheckpoint checkpoint = new ZoneCrawlCheckpoint();

      assertThatThrownBy(() -> checkpoint.completeGrid(1, Set.of("101"), 100))
              .isInstanceOf(IllegalStateException.class)
              .hasMessageContaining("expected=0, actual=1");
   }
}
