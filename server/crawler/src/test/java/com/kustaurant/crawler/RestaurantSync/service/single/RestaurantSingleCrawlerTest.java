package com.kustaurant.crawler.RestaurantSync.service.single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.crawler.RestaurantSync.service.zone.ZoneResultPolicy;
import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.kustaurant.map.ZoneType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class RestaurantSingleCrawlerTest {

   @Test
   @SuppressWarnings("unchecked")
   void usesApolloDataWithoutOpeningTheMenuPage() {
      PlaywrightManager playwrightManager = mock(PlaywrightManager.class);
      RestaurantPageDriver pageDriver = mock(RestaurantPageDriver.class);
      RestaurantMenuExtractor menuExtractor = mock(RestaurantMenuExtractor.class);
      ZoneResultPolicy zoneResultPolicy = mock(ZoneResultPolicy.class);
      Page page = mock(Page.class);

      RestaurantSingleCrawler crawler = new RestaurantSingleCrawler(
              playwrightManager,
              pageDriver,
              new RestaurantResponseCollector(),
              new NaverApolloStateParser(new ObjectMapper()),
              new RestaurantInfoExtractor(),
              menuExtractor,
              new RestaurantMenuDataResolver(),
              zoneResultPolicy
      );

      String placeUrl = "https://map.naver.com/p/entry/place/42";
      String detailUrl = "https://pcmap.place.naver.com/place/42?entry=bmp&from=map";
      String html = """
              <script>
              window.__APOLLO_STATE__ = {
                "PlaceDetailBase:42": {
                  "__typename":"PlaceDetailBase",
                  "id":"42",
                  "name":"Apollo 식당",
                  "category":"한식",
                  "roadAddress":"서울 광진구 테스트로 42",
                  "virtualPhone":"0507-0000-0042",
                  "coordinate":{"x":"127.1","y":"37.5"}
                },
                "Menu:42_0": {
                  "__typename":"Menu", "id":"42_0", "name":"테스트 메뉴", "price":"12000", "images":[]
                },
                "ROOT_QUERY": {
                  "placeDetail": {
                    "__typename":"PlaceDetail",
                    "base":{"__ref":"PlaceDetailBase:42"},
                    "menus":[{"__ref":"Menu:42_0"}]
                  }
                }
              };
              </script>
              """;

      Response response = mock(Response.class);
      when(response.url()).thenReturn(detailUrl);
      when(response.headers()).thenReturn(Map.of("content-type", "text/html; charset=utf-8"));
      when(response.text()).thenReturn(html);
      when(page.url()).thenReturn(placeUrl);
      when(page.frames()).thenReturn(List.of());
      when(zoneResultPolicy.resolveZoneType(any())).thenReturn(ZoneType.OUT_OF_ZONE);

      AtomicReference<Consumer<Response>> responseHandler = new AtomicReference<>();
      org.mockito.Mockito.doAnswer(invocation -> {
         responseHandler.set(invocation.getArgument(0));
         return null;
      }).when(page).onResponse(any());
      org.mockito.Mockito.doAnswer(invocation -> {
         responseHandler.get().accept(response);
         return null;
      }).when(pageDriver).openPlacePage(page, placeUrl);
      when(playwrightManager.crawl(any())).thenAnswer(invocation -> {
         Function<Page, ?> action = invocation.getArgument(0);
         return action.apply(page);
      });

      var result = crawler.crawl(placeUrl);

      assertThat(result.placeName()).isEqualTo("Apollo 식당");
      assertThat(result.restaurantAddress()).isEqualTo("서울 광진구 테스트로 42");
      assertThat(result.latitude()).isEqualTo(37.5);
      assertThat(result.longitude()).isEqualTo(127.1);
      assertThat(result.menus()).singleElement().satisfies(menu -> {
         assertThat(menu.menuName()).isEqualTo("테스트 메뉴");
         assertThat(menu.menuPrice()).isEqualTo("12,000원");
      });
      verify(pageDriver, never()).clickMenuTab(page);
      verify(pageDriver, never()).navigateToDirectMenuIfNeeded(page, "42", false);
      verify(menuExtractor, never()).extractMenus(any(), any());
   }
}
