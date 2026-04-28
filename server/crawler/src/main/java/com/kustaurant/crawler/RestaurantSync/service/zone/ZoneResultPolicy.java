package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.map.CoordinateV2;
import com.kustaurant.map.MapConstantsV2;
import com.kustaurant.map.ZonePolygon;
import com.kustaurant.map.ZoneType;
import com.kustaurant.map.utils.PolygonUtils;
import com.kustaurant.restaurantSync.RestaurantRaw;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ZoneResultPolicy {

   public Optional<ZonePolygon> findZonePolygon(ZoneType crawlScope) {
      return MapConstantsV2.ZONES.stream()
              .filter(zone -> zone.zoneType() == crawlScope)
              .findFirst();
   }

   public boolean isMeaningfulResult(RestaurantRaw result) {
      return result != null
              && !isBlank(result.sourcePlaceId())
              && !isBlank(result.placeName())
              && result.latitude() != null
              && result.longitude() != null;
   }

   public boolean isCompleteFailure(RestaurantRaw result) {
      if (result == null) return true;

      boolean noBasic = isBlank(result.placeName())
              && isBlank(result.category())
              && isBlank(result.restaurantAddress())
              && isBlank(result.phoneNumber());
      int menuCount = result.menus() == null ? 0 : result.menus().size();
      return noBasic && menuCount == 0;
   }

   public boolean isPlaceInsideZone(RestaurantRaw result, ZonePolygon zone) {
      CoordinateV2 point = new CoordinateV2(result.latitude(), result.longitude());
      return PolygonUtils.isPointInsidePolygon(point, zone.coordinates());
   }

   public ZoneType resolveZoneType(CoordinateV2 coord) {
      for (ZonePolygon zone : MapConstantsV2.ZONES) {
         if (PolygonUtils.isPointInsidePolygon(coord, zone.coordinates())) {
            return zone.zoneType();
         }
      }

      return ZoneType.OUT_OF_ZONE;
   }

   private boolean isBlank(String value) {
      return value == null || value.isBlank();
   }
}
