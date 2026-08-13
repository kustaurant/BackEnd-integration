package com.kustaurant.crawler.RestaurantSync.service.zone;

import java.util.LinkedHashSet;
import java.util.Set;

final class ZoneCrawlCheckpoint {
   private final LinkedHashSet<String> discoveredPlaceIds = new LinkedHashSet<>();
   private int nextGridIndex;

   synchronized int nextGridIndex() {
      return nextGridIndex;
   }

   synchronized int discoveredPlaceCount() {
      return discoveredPlaceIds.size();
   }

   synchronized void completeGrid(int completedGridIndex, Set<String> foundPlaceIds, int maxPlaceIds) {
      if (completedGridIndex != nextGridIndex) {
         throw new IllegalStateException(
                 "체크포인트 그리드 순서가 일치하지 않습니다. expected="
                         + nextGridIndex + ", actual=" + completedGridIndex
         );
      }

      for (String placeId : foundPlaceIds) {
         discoveredPlaceIds.add(placeId);
         if (discoveredPlaceIds.size() >= maxPlaceIds) {
            break;
         }
      }
      nextGridIndex++;
   }

   synchronized Set<String> discoveredPlaceIdsSnapshot() {
      return new LinkedHashSet<>(discoveredPlaceIds);
   }
}
