package com.kustaurant.crawler.RestaurantSync.service.zone;

public class NaverCaptchaRequiredException extends RuntimeException {
   private final int gridRow;
   private final int gridCol;

   public NaverCaptchaRequiredException(int gridRow, int gridCol) {
      super("네이버 보안 인증이 필요합니다. grid=" + gridRow + "," + gridCol);
      this.gridRow = gridRow;
      this.gridCol = gridCol;
   }

   public int gridRow() {
      return gridRow;
   }

   public int gridCol() {
      return gridCol;
   }

   public String grid() {
      return gridRow + "," + gridCol;
   }
}
