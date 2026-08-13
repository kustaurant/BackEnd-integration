package com.kustaurant.crawler.RestaurantSync.service.single;

import static org.assertj.core.api.Assertions.assertThat;

import com.kustaurant.crawler.RestaurantSync.service.single.NaverApolloStateParser.NaverPlaceData;
import com.kustaurant.restaurantSync.RestaurantRawMenu;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class RestaurantMenuDataResolverTest {
   private final RestaurantMenuDataResolver resolver = new RestaurantMenuDataResolver();

   @Test
   void usesApolloMenusWhenTheMenuListIsAuthoritative() {
      RestaurantRawMenu apolloMenu = new RestaurantRawMenu("Apollo 메뉴", "10,000원", null);
      NaverPlaceData data = placeData(List.of(apolloMenu), true);

      List<RestaurantRawMenu> result = resolver.resolve(
              Optional.of(data),
              () -> List.of(new RestaurantRawMenu("DOM 메뉴", "11,000원", null))
      );

      assertThat(result).containsExactly(apolloMenu);
   }

   @Test
   void usesDomMenusWhenApolloOnlyContainsUnverifiedMenuEntities() {
      RestaurantRawMenu cachedFragment = new RestaurantRawMenu("일부 캐시 메뉴", "10,000원", null);
      RestaurantRawMenu domMenu = new RestaurantRawMenu("DOM 전체 메뉴", "11,000원", null);
      AtomicBoolean domRead = new AtomicBoolean();
      NaverPlaceData data = placeData(List.of(cachedFragment), false);

      List<RestaurantRawMenu> result = resolver.resolve(Optional.of(data), () -> {
         domRead.set(true);
         return List.of(domMenu);
      });

      assertThat(domRead).isTrue();
      assertThat(result).containsExactly(domMenu);
   }

   private NaverPlaceData placeData(List<RestaurantRawMenu> menus, boolean menuDataPresent) {
      return new NaverPlaceData(
              "테스트 식당", null, null, null, null, null, null, menus, menuDataPresent
      );
   }
}
