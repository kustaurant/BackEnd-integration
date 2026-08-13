package com.kustaurant.crawler.RestaurantSync.service.single;

import com.microsoft.playwright.Response;
import java.net.URI;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RestaurantResponseCollector {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(RestaurantResponseCollector.class);

   public void captureHtmlResponse(
           Response response,
           String placeId,
           AtomicReference<String> homeHtmlRef,
           AtomicReference<String> menuHtmlRef,
           boolean analyzeMode
   ) {
      captureHtmlResponse(response, placeId, homeHtmlRef, menuHtmlRef, analyzeMode, true);
   }

   public void captureHtmlResponse(
           Response response,
           String placeId,
           AtomicReference<String> homeHtmlRef,
           AtomicReference<String> menuHtmlRef,
           boolean analyzeMode,
           boolean captureInitialPlaceDocument
   ) {
      try {
         String url = response.url();
         String contentType = ((String) response.headers().getOrDefault("content-type", ""))
                 .toLowerCase(Locale.ROOT);
         if (!contentType.contains("html") || placeId == null) {
            return;
         }

         if (isHomeDocument(url, placeId, captureInitialPlaceDocument)) {
            captureHomeHtml(url, response, homeHtmlRef, analyzeMode);
            return;
         }

         if (isMenuDocument(url, placeId)) {
            captureMenuHtml(url, response, menuHtmlRef, analyzeMode);
         }
      } catch (Exception e) {
         if (analyzeMode) {
            log.warn("html 응답 캡처 중 오류", e);
         }
      }
   }

   private boolean isHomeDocument(String url, String placeId, boolean captureInitialPlaceDocument) {
      String path = pathOf(url);
      return (captureInitialPlaceDocument && path.equals("/place/" + placeId))
              || path.equals("/place/" + placeId + "/home")
              || path.equals("/restaurant/" + placeId)
              || path.equals("/restaurant/" + placeId + "/home");
   }

   private boolean isMenuDocument(String url, String placeId) {
      String path = pathOf(url);
      return path.equals("/place/" + placeId + "/menu")
              || path.equals("/restaurant/" + placeId + "/menu");
   }

   private String pathOf(String url) {
      try {
         URI uri = URI.create(url);
         if (!"pcmap.place.naver.com".equalsIgnoreCase(uri.getHost())) {
            return "";
         }
         String path = uri.getPath() == null ? "" : uri.getPath();
         return path.length() > 1 && path.endsWith("/")
                 ? path.substring(0, path.length() - 1)
                 : path;
      } catch (IllegalArgumentException ignored) {
         return "";
      }
   }

   private void captureHomeHtml(
           String url,
           Response response,
           AtomicReference<String> homeHtmlRef,
           boolean analyzeMode
   ) {
      String html = response.text();
      if (isBlank(html)) {
         if (analyzeMode) {
            log.info("비어있는 home html 응답 무시. url={}", url);
         }
         return;
      }

      boolean isDetailedUrl = url.contains("?") && url.contains("from=map");
      boolean newHasMarkers = hasHomeMarkers(html);
      String current = homeHtmlRef.get();
      if (isBlank(current)) {
         homeHtmlRef.set(html);
         if (analyzeMode) {
            log.info("home html 응답 캡처. url={}", url);
         }
         return;
      }

      boolean currentHasMarkers = hasHomeMarkers(current);
      if (isDetailedUrl && !currentHasMarkers) {
         homeHtmlRef.set(html);
         if (analyzeMode) {
            log.info("상세 home 응답으로 교체. url={}", url);
         }
         return;
      }

      if (!currentHasMarkers && newHasMarkers) {
         homeHtmlRef.set(html);
         if (analyzeMode) {
            log.info("더 완전한 home 응답으로 교체. url={}", url);
         }
         return;
      }

      if (analyzeMode) {
         log.info("기존 home 캡처 유지. url={}", url);
      }
   }

   private void captureMenuHtml(
           String url,
           Response response,
           AtomicReference<String> menuHtmlRef,
           boolean analyzeMode
   ) {
      if (menuHtmlRef.get() != null) {
         return;
      }

      String html = response.text();
      if (isBlank(html)) {
         if (analyzeMode) {
            log.info("비어있는 menu html 응답 무시. url={}", url);
         }
         return;
      }

      boolean hasMenuMarkers = html.contains("메뉴")
              || html.contains("place_section_content")
              || html.contains("li")
              || html.contains("menu");

      if (!hasMenuMarkers) {
         if (analyzeMode) {
            log.info("유효하지 않은 menu html 응답 무시. url={}", url);
         }
         return;
      }

      menuHtmlRef.set(html);
      if (analyzeMode) {
         log.info("menu html 응답 캡처. url={}", url);
      }
   }

   private boolean hasHomeMarkers(String html) {
      if (isBlank(html)) {
         return false;
      }
      return html.contains("GHAhO")
              || html.contains("lnJFt")
              || html.contains("PIbes")
              || html.contains("xlx7Q")
              || html.contains("window.__APOLLO_STATE__")
              || html.contains("주소")
              || html.contains("전화번호")
              || html.contains("og:title");
   }

   private boolean isBlank(String value) {
      return value == null || value.isBlank();
   }
}
