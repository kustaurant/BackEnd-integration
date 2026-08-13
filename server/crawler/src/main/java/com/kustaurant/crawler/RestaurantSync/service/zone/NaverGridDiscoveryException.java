package com.kustaurant.crawler.RestaurantSync.service.zone;

public class NaverGridDiscoveryException extends RuntimeException {
   public NaverGridDiscoveryException(int gridRow, int gridCol, Throwable cause) {
      super("네이버 지도 그리드 수집에 실패했습니다. grid=" + gridRow + "," + gridCol, cause);
   }
}
