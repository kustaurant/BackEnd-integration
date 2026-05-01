package com.kustaurant.crawler.RestaurantSync.service.single;

import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.kustaurant.crawler.RestaurantSync.service.zone.ZoneResultPolicy;
import com.kustaurant.map.CoordinateV2;
import com.kustaurant.restaurantSync.RestaurantRawMenu;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.map.ZoneType;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantSingleCrawler {

   private static final int MAX_RETRIES = 2;
   private static final String NAVER_PLACE_NOT_FOUND_TEXT = "요청하신 페이지를 찾을 수 없습니다.";

   private final PlaywrightManager playwrightManager;
   private final RestaurantPageDriver pageDriver;
   private final RestaurantResponseCollector responseCollector;
   private final RestaurantInfoExtractor infoExtractor;
   private final RestaurantMenuExtractor menuExtractor;
   private final ZoneResultPolicy zoneResultPolicy;

   public RestaurantRaw crawl(String placeUrl) {
      return crawlWithRetry(placeUrl, false);
   }

   public RestaurantRaw analyze(String placeUrl) {
      return crawlWithRetry(placeUrl, true);
   }

   private RestaurantRaw crawlWithRetry(String placeUrl, boolean analyzeMode) {
      RestaurantRaw last = null;

      for (int attempt = 1; attempt <= MAX_RETRIES + 1; attempt++) {
         last = crawlInternal(placeUrl, analyzeMode);
         if (!shouldRetryWithBackoff(last, attempt)) return last;

         long backoffMs = retryBackoffMillis(attempt);
         if (analyzeMode) {
            log.warn(
                    "기본정보가 비어 재시도합니다. attempt={}/{}, waitMs={}, sourcePlaceId={}",
                    attempt, MAX_RETRIES + 1, backoffMs, last == null ? null : last.sourcePlaceId()
            );
         }
         sleepMillis(backoffMs);
      }

      return last;
   }

   private RestaurantRaw crawlInternal(String placeUrl, boolean analyzeMode) {
      return playwrightManager.crawl(page -> {
         String placeId = infoExtractor.extractPlaceId(placeUrl);

         AtomicReference<String> homeHtmlRef = new AtomicReference<>();
         AtomicReference<String> menuHtmlRef = new AtomicReference<>();

         page.onResponse(response -> responseCollector.captureHtmlResponse(
                 response, placeId, homeHtmlRef, menuHtmlRef, analyzeMode
         ));

         if (analyzeMode) {
            log.info("=== 네이버플레이스 분석 시작 ===");
            log.info("대상 URL={}", placeUrl);
         }

         pageDriver.openPlacePage(page, placeUrl);
         waitForHomeCapture(page, homeHtmlRef, 6_000);
         hydrateHomeHtmlFromEntryFrameIfMissing(page, homeHtmlRef, analyzeMode, placeId);

         boolean menuClicked = pageDriver.clickMenuTab(page);
         if (analyzeMode) log.info("메뉴 탭 클릭 여부={}", menuClicked);


         if (menuClicked) pageDriver.waitForMenuIdle(page);

         waitForHomeCapture(page, homeHtmlRef, 6_000);
         hydrateHomeHtmlFromEntryFrameIfMissing(page, homeHtmlRef, analyzeMode, placeId);

         if (isBlank(homeHtmlRef.get())) {
            pageDriver.navigateToDirectHomeIfNeeded(page, placeId, false);
            waitForHomeCapture(page, homeHtmlRef, 8_000);
            hydrateHomeHtmlFromEntryFrameIfMissing(page, homeHtmlRef, analyzeMode, placeId);
            captureHomeHtmlFromCurrentPageIfMissing(page, homeHtmlRef, analyzeMode, placeId);
         }

         String liveHomeSnapshot = safeEntryFrameContent(page);
         if (isBlank(liveHomeSnapshot)) liveHomeSnapshot = safePageContent(page);


         pageDriver.navigateToDirectMenuIfNeeded(page, placeId, menuHtmlRef.get() != null);
         waitForHtmlCapture(page, menuHtmlRef, 8_000);

         String homeHtml = homeHtmlRef.get();
         String menuHtml = menuHtmlRef.get();
         Document homeDoc = isBlank(homeHtml) ? null : Jsoup.parse(homeHtml);
         Document menuDoc = isBlank(menuHtml) ? null : Jsoup.parse(menuHtml);

         String sourceUrl = safePageUrl(page);
         RestaurantInfoExtractor.NaverPlaceBasicInfo basicInfo = infoExtractor.extract(homeDoc, homeHtml);

         if (hasNoBasicInfo(basicInfo) && !isBlank(liveHomeSnapshot)) {
            Document liveDoc = Jsoup.parse(liveHomeSnapshot);
            RestaurantInfoExtractor.NaverPlaceBasicInfo liveBasicInfo = infoExtractor.extract(liveDoc, liveHomeSnapshot);

            if (!hasNoBasicInfo(liveBasicInfo)) {
               basicInfo = liveBasicInfo;
               if (analyzeMode) {
                  log.info("live DOM fallback으로 기본정보 추출 성공. sourcePlaceId={}", placeId);
               }
            } else if (analyzeMode) {
               log.warn("live DOM fallback에서도 기본정보 추출 실패. sourcePlaceId={}", placeId);
            }
         }

         List<RestaurantRawMenu> menus = menuExtractor.extractMenus(menuDoc, page);

         Double latitude = basicInfo.latitude();
         Double longitude = basicInfo.longitude();

         ZoneType zoneType = null;
         if (latitude != null && longitude != null) {
            zoneType = zoneResultPolicy.resolveZoneType(new CoordinateV2(latitude, longitude));
         } else if (analyzeMode) {
            log.warn("좌표 추출 누락. sourcePlaceId={}, placeName={}, lat={}, lng={}",
                    placeId, basicInfo.placeName(), latitude, longitude);
         }

         RestaurantRaw result = new RestaurantRaw(
                 placeId,
                 isBlank(sourceUrl) ? placeUrl : sourceUrl,
                 basicInfo.placeName(),
                 basicInfo.category(),
                 basicInfo.restaurantAddress(),
                 basicInfo.phoneNumber(),
                 latitude,
                 longitude,
                 basicInfo.imageUrl(),
                 zoneType,
                 menus
         );

         if (analyzeMode) {
            if (isBlank(homeHtml)) {
               log.warn("home html 캡처 실패. sourcePlaceId={}, placeId={}", placeId, placeUrl);
            }
            log.info(
                    " === 네이버플레이스 분석 완료. sourcePlaceId={}, placeName={}, category={}, restaurantAddress={}, phone={}, lat={}, lng={}, zoneType={}, zoneDescription={}, menuCount={}",
                    result.sourcePlaceId(), result.placeName(), result.category(), result.restaurantAddress(),
                    result.phoneNumber(), result.latitude(), result.longitude(),
                    zoneType, zoneType == null ? null : zoneType.getDescription(),
                    result.menus() == null ? 0 : result.menus().size()
            );
         } else {
            log.info(
                    "네이버플레이스 크롤 완료. sourcePlaceId={}, placeName={}, category={}, restaurantAddress={}, phone={}, lat={}, lng={}, menuCount={}",
                    result.sourcePlaceId(), result.placeName(), result.category(), result.restaurantAddress(),
                    result.phoneNumber(), result.latitude(), result.longitude(),
                    result.menus() == null ? 0 : result.menus().size()
            );
         }

         return result;
      });
   }

   private boolean shouldRetryWithBackoff(RestaurantRaw result, int attempt) {
      if (attempt > MAX_RETRIES) return false;
      if (result == null) return true;
      if (isNaverPlaceNotFoundResult(result)) return false;

      boolean noBasic = isBlank(result.placeName()) && isBlank(result.category())
              && isBlank(result.restaurantAddress()) && isBlank(result.phoneNumber());
      int menuCount = result.menus() == null ? 0 : result.menus().size();
      boolean missingCoordinates = result.latitude() == null || result.longitude() == null;
      return (noBasic && menuCount == 0) || missingCoordinates;
   }

   private boolean isNaverPlaceNotFoundResult(RestaurantRaw result) {
      String address = result.restaurantAddress();
      return address != null && address.contains(NAVER_PLACE_NOT_FOUND_TEXT);
   }

   private long retryBackoffMillis(int attempt) {
      if (attempt == 1) return 30_000;
      return 60_000;
   }

   private void sleepMillis(long millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   private String safePageUrl(Page page) {
      try {
         return page.url();
      } catch (Exception ignored) {
         return null;
      }
   }

   private boolean isBlank(String value) {
      return value == null || value.isBlank();
   }

   private void waitForHtmlCapture(Page page, AtomicReference<String> htmlRef, long timeoutMs) {
      long start = System.currentTimeMillis();
      while (System.currentTimeMillis() - start < timeoutMs) {
         if (!isBlank(htmlRef.get())) {
            return;
         }
         page.waitForTimeout(150);
      }
   }

   private void waitForHomeCapture(Page page, AtomicReference<String> homeHtmlRef, long timeoutMs) {
      long start = System.currentTimeMillis();
      String lastObserved = null;
      while (System.currentTimeMillis() - start < timeoutMs) {
         String html = homeHtmlRef.get();
         if (!isBlank(html)) {
            if (html.contains("GHAhO")
                    || html.contains("lnJFt")
                    || html.contains("og:title")
                    || html.contains("PIbes")) {
               return;
            }
            lastObserved = html;
         }
         page.waitForTimeout(150);
      }

      if (!isBlank(homeHtmlRef.get())) {
         return;
      }
      if (!isBlank(lastObserved)) {
         homeHtmlRef.set(lastObserved);
      }
   }

   private void captureHomeHtmlFromCurrentPageIfMissing(
           Page page, AtomicReference<String> homeHtmlRef, boolean analyzeMode, String placeId
   ) {
      if (!isBlank(homeHtmlRef.get())) return;

      try {
         String html = safeEntryFrameContent(page);
         if (isBlank(html)) html = page.content();
         if (isBlank(html)) return;

         if (!looksLikeHomeHtml(html)) {
            if (analyzeMode) {
               log.warn("fallback page.content를 home html로 인식하지 못함. sourcePlaceId={}", placeId);
            }
            return;
         }

         homeHtmlRef.set(html);
         if (analyzeMode) {
            log.info("현재 페이지 content fallback으로 home html 캡처 성공. sourcePlaceId={}", placeId);
         }
      } catch (Exception e) {
         if (analyzeMode) {
            log.warn("현재 페이지 content fallback home html 캡처 실패. sourcePlaceId={}", placeId, e);
         }
      }
   }

   private void hydrateHomeHtmlFromEntryFrameIfMissing(
           Page page,
           AtomicReference<String> homeHtmlRef,
           boolean analyzeMode,
           String placeId
   ) {
      if (!isBlank(homeHtmlRef.get())) {
         return;
      }

      try {
         String html = safeEntryFrameContent(page);
         if (isBlank(html) || !looksLikeHomeHtml(html)) {
            return;
         }

         homeHtmlRef.set(html);
         if (analyzeMode) {
            log.info("entry iframe fallback으로 home html 캡처 성공. sourcePlaceId={}", placeId);
         }
      } catch (Exception e) {
         if (analyzeMode) {
            log.warn("entry iframe fallback home html 캡처 실패. sourcePlaceId={}", placeId, e);
         }
      }
   }

   private boolean looksLikeHomeHtml(String html) {
      return html.contains("GHAhO")
              || html.contains("lnJFt")
              || html.contains("PIbes")
              || html.contains("og:title")
              || html.contains("address")
              || html.contains("restaurant");
   }

   private String safePageContent(Page page) {
      try {
         return page.content();
      } catch (Exception ignored) {
         return null;
      }
   }

   private String safeEntryFrameContent(Page page) {
      try {
         Optional<Frame> entryFrame = page.frames().stream()
                 .filter(frame -> "entryIframe".equals(frame.name()))
                 .findFirst();
          return entryFrame.map(Frame::content).orElse(null);
      } catch (Exception ignored) {
         return null;
      }
   }

   private boolean hasNoBasicInfo(RestaurantInfoExtractor.NaverPlaceBasicInfo basicInfo) {
      if (basicInfo == null) return true;

      return isBlank(basicInfo.placeName())
              && isBlank(basicInfo.category())
              && isBlank(basicInfo.restaurantAddress())
              && isBlank(basicInfo.phoneNumber());
   }
}
