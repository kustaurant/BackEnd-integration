package com.kustaurant.crawler.RestaurantSync.service.single;

import com.kustaurant.crawler.RestaurantSync.service.single.NaverApolloStateParser.NaverPlaceData;
import com.kustaurant.restaurantSync.RestaurantRawMenu;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMenuDataResolver {

   public List<RestaurantRawMenu> resolve(
           Optional<NaverPlaceData> apolloData,
           Supplier<List<RestaurantRawMenu>> domMenus
   ) {
      return apolloData
              .filter(NaverPlaceData::menuDataPresent)
              .map(NaverPlaceData::menus)
              .orElseGet(domMenus);
   }
}
