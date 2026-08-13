package com.kustaurant.crawler.RestaurantSync.service.single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.microsoft.playwright.Response;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class RestaurantResponseCollectorTest {

   private final RestaurantResponseCollector collector = new RestaurantResponseCollector();

   @Test
   void capturesInitialPlaceDocumentAsHomeHtml() {
      Response response = htmlResponse(
              "https://pcmap.place.naver.com/place/1183785127?entry=bmp&from=map",
              "<script>window.__APOLLO_STATE__ = {}</script>"
      );
      AtomicReference<String> home = new AtomicReference<>();
      AtomicReference<String> menu = new AtomicReference<>();

      collector.captureHtmlResponse(response, "1183785127", home, menu, false);

      assertThat(home.get()).contains("window.__APOLLO_STATE__");
      assertThat(menu.get()).isNull();
   }

   @Test
   void legacyModeIgnoresInitialPlaceDocument() {
      Response response = htmlResponse(
              "https://pcmap.place.naver.com/place/1183785127?entry=bmp&from=map",
              "<script>window.__APOLLO_STATE__ = {}</script>"
      );
      AtomicReference<String> home = new AtomicReference<>();
      AtomicReference<String> menu = new AtomicReference<>();

      collector.captureHtmlResponse(response, "1183785127", home, menu, false, false);

      assertThat(home.get()).isNull();
      assertThat(menu.get()).isNull();
   }

   @Test
   void capturesDirectRestaurantMenuDocument() {
      Response response = htmlResponse(
              "https://pcmap.place.naver.com/restaurant/1183785127/menu",
              "<html><body><ul><li>규카츠 정식</li></ul></body></html>"
      );
      AtomicReference<String> home = new AtomicReference<>();
      AtomicReference<String> menu = new AtomicReference<>();

      collector.captureHtmlResponse(response, "1183785127", home, menu, false);

      assertThat(home.get()).isNull();
      assertThat(menu.get()).contains("규카츠 정식");
   }

   @Test
   void ignoresLookalikePathsFromAnotherHost() {
      Response response = htmlResponse(
              "https://example.com/place/1183785127",
              "<script>window.__APOLLO_STATE__ = {}</script>"
      );
      AtomicReference<String> home = new AtomicReference<>();
      AtomicReference<String> menu = new AtomicReference<>();

      collector.captureHtmlResponse(response, "1183785127", home, menu, false);

      assertThat(home.get()).isNull();
      assertThat(menu.get()).isNull();
   }

   private Response htmlResponse(String url, String body) {
      Response response = mock(Response.class);
      when(response.url()).thenReturn(url);
      when(response.headers()).thenReturn(Map.of("content-type", "text/html; charset=utf-8"));
      when(response.text()).thenReturn(body);
      return response;
   }
}
