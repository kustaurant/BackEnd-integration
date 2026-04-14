package com.kustaurant.crawler.RestaurantSync.service.single;

import com.microsoft.playwright.Response;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NaverPlaceResponseCollector {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(NaverPlaceResponseCollector.class);

   public void captureHtmlResponse(Response response, String placeId, AtomicReference homeHtmlRef, AtomicReference menuHtmlRef, boolean analyzeMode) {
      try {
         String url = response.url();
         String contentType = ((String)response.headers().getOrDefault("content-type", "")).toLowerCase(Locale.ROOT);
         if (!contentType.contains("html") || placeId == null) {
            return;
         }

         String homePath = "/restaurant/" + placeId + "/home";
         String menuPath = "/restaurant/" + placeId + "/menu";
         if (url.contains(homePath)) {
            this.captureHomeHtml(url, response, homeHtmlRef, analyzeMode);
            return;
         }

         if (url.contains(menuPath)) {
            this.captureMenuHtml(url, response, menuHtmlRef, analyzeMode);
         }
      } catch (Exception e) {
         if (analyzeMode) {
            log.warn("html 응답 캡처 중 오류", e);
         }
      }

   }

   private void captureHomeHtml(String url, Response response, AtomicReference homeHtmlRef, boolean analyzeMode) {
      String html = response.text();
      if (this.isBlank(html)) {
         if (analyzeMode) {
            log.info("비어있는 home html 응답 무시. url={}", url);
         }

      } else {
         boolean isDetailedUrl = url.contains("?") && url.contains("from=map");
         boolean newHasMarkers = this.hasHomeMarkers(html);
         String current = (String)homeHtmlRef.get();
         if (this.isBlank(current)) {
            homeHtmlRef.set(html);
            if (analyzeMode) {
               log.info("captured home html response. url={}", url);
            }

         } else {
            boolean currentHasMarkers = this.hasHomeMarkers(current);
            if (isDetailedUrl && !currentHasMarkers) {
               homeHtmlRef.set(html);
               if (analyzeMode) {
                  log.info("상세 home 응답으로 교체. url={}", url);
               }

            } else if (!currentHasMarkers && newHasMarkers) {
               homeHtmlRef.set(html);
               if (analyzeMode) {
                  log.info("더 완전한 home 응답으로 교체. url={}", url);
               }

            } else {
               if (analyzeMode) {
                  log.info("기존 home 캡처 유지. url={}", url);
               }

            }
         }
      }
   }

   private void captureMenuHtml(String url, Response response, AtomicReference menuHtmlRef, boolean analyzeMode) {
      if (menuHtmlRef.get() == null) {
         String html = response.text();
         if (this.isBlank(html)) {
            if (analyzeMode) {
               log.info("비어있는 menu html 응답 무시. url={}", url);
            }

         } else {
            boolean hasMenuMarkers = html.contains("메뉴") || html.contains("place_section_content") || html.contains("li") || html.contains("menu");
            if (!hasMenuMarkers) {
               if (analyzeMode) {
                  log.info("유효하지 않은 menu html 응답 무시. url={}", url);
               }

            } else {
               menuHtmlRef.set(html);
               if (analyzeMode) {
                  log.info("captured menu html response. url={}", url);
               }

            }
         }
      }
   }

   private boolean hasHomeMarkers(String html) {
      if (this.isBlank(html)) {
         return false;
      } else {
         return html.contains("GHAhO") || html.contains("lnJFt") || html.contains("PIbes") || html.contains("xlx7Q") || html.contains("주소") || html.contains("전화번호") || html.contains("og:title");
      }
   }

   private boolean isBlank(String value) {
      return value == null || value.isBlank();
   }
}
