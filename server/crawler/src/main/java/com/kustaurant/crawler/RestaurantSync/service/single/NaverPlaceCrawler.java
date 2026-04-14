package com.kustaurant.crawler.RestaurantSync.service.single;

import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.NaverPlaceMenu;
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
public class NaverPlaceCrawler {

   private final PlaywrightManager playwrightManager;
   private final NaverPlacePageDriver pageDriver;
   private final NaverPlaceResponseCollector responseCollector;
   private final NaverPlaceInfoExtractor infoExtractor;
   private final NaverPlaceMenuExtractor menuExtractor;

   private static final int MAX_RETRIES = 2;

   public NaverPlaceCrawlResult crawl(String placeUrl) {
      return crawlWithRetry(placeUrl, false);
   }

   public NaverPlaceCrawlResult analyze(String placeUrl) {
      return crawlWithRetry(placeUrl, true);
   }

   private NaverPlaceCrawlResult crawlWithRetry(String placeUrl, boolean analyzeMode) {
      NaverPlaceCrawlResult last = null;

      for (int attempt = 1; attempt <= MAX_RETRIES + 1; attempt++) {
         last = crawlInternal(placeUrl, analyzeMode);
         if (!shouldRetryWithBackoff(last, attempt)) {
            return last;
         }

         long backoffMs = retryBackoffMillis(attempt);
         if (analyzeMode) {
            log.warn(
                    "기본정보가 비어 재시도합니다. attempt={}/{}, waitMs={}, sourcePlaceId={}",
                    attempt,
                    MAX_RETRIES + 1,
                    backoffMs,
                    last == null ? null : last.sourcePlaceId()
            );
         }
         sleepMillis(backoffMs);
      }

      return last;
   }

   private NaverPlaceCrawlResult crawlInternal(String placeUrl, boolean analyzeMode) {
      return playwrightManager.crawl(page -> {
         String placeId = infoExtractor.extractPlaceId(placeUrl);

         AtomicReference<String> homeHtmlRef = new AtomicReference<>();
         AtomicReference<String> menuHtmlRef = new AtomicReference<>();

         page.onResponse(response -> responseCollector.captureHtmlResponse(
                 response,
                 placeId,
                 homeHtmlRef,
                 menuHtmlRef,
                 analyzeMode
         ));

         if (analyzeMode) {
            log.info("=== 네이버 플레이스 분석 시작 ===");
            log.info("대상 URL={}", placeUrl);
         }

         pageDriver.openPlacePage(page, placeUrl);
         waitForHomeCapture(page, homeHtmlRef, 6_000);
         hydrateHomeHtmlFromEntryFrameIfMissing(page, homeHtmlRef, analyzeMode, placeId);

         boolean menuClicked = pageDriver.clickMenuTab(page);
         if (analyzeMode) {
            log.info("메뉴 탭 클릭 여부={}", menuClicked);
         }

         if (menuClicked) {
            pageDriver.waitForMenuIdle(page);
         }
         waitForHomeCapture(page, homeHtmlRef, 6_000);
         hydrateHomeHtmlFromEntryFrameIfMissing(page, homeHtmlRef, analyzeMode, placeId);

         if (isBlank(homeHtmlRef.get())) {
            pageDriver.navigateToDirectHomeIfNeeded(page, placeId, false);
            waitForHomeCapture(page, homeHtmlRef, 8_000);
            hydrateHomeHtmlFromEntryFrameIfMissing(page, homeHtmlRef, analyzeMode, placeId);
            captureHomeHtmlFromCurrentPageIfMissing(page, homeHtmlRef, analyzeMode, placeId);
         }

         // 메뉴 페이지로 이동하기 전에 현재 entry 페이지 HTML을 백업해 기본정보 추출 fallback으로 사용한다.
         String liveHomeSnapshot = safeEntryFrameContent(page);
         if (isBlank(liveHomeSnapshot)) {
            liveHomeSnapshot = safePageContent(page);
         }

         pageDriver.navigateToDirectMenuIfNeeded(page, placeId, menuHtmlRef.get() != null);
         waitForHtmlCapture(page, menuHtmlRef, 8_000);

         String homeHtml = homeHtmlRef.get();
         String menuHtml = menuHtmlRef.get();
         Document homeDoc = isBlank(homeHtml) ? null : Jsoup.parse(homeHtml);
         Document menuDoc = isBlank(menuHtml) ? null : Jsoup.parse(menuHtml);

         String sourceUrl = safePageUrl(page);
         NaverPlaceInfoExtractor.NaverPlaceBasicInfo basicInfo = infoExtractor.extract(homeDoc, homeHtml);
         if (hasNoBasicInfo(basicInfo) && !isBlank(liveHomeSnapshot)) {
            Document liveDoc = Jsoup.parse(liveHomeSnapshot);
            NaverPlaceInfoExtractor.NaverPlaceBasicInfo liveBasicInfo = infoExtractor.extract(liveDoc, liveHomeSnapshot);
            if (!hasNoBasicInfo(liveBasicInfo)) {
               basicInfo = liveBasicInfo;
               if (analyzeMode) {
                  log.info("live DOM fallback으로 기본정보 추출 성공. sourcePlaceId={}", placeId);
               }
            } else if (analyzeMode) {
               log.warn("live DOM fallback에서도 기본정보 추출 실패. sourcePlaceId={}", placeId);
            }
         }
         List<NaverPlaceMenu> menus = menuExtractor.extractMenus(menuDoc, page);

         NaverPlaceCrawlResult result = new NaverPlaceCrawlResult(
                 placeId,
                 isBlank(sourceUrl) ? placeUrl : sourceUrl,
                 basicInfo.placeName(),
                 basicInfo.category(),
                 basicInfo.restaurantAddress(),
                 basicInfo.phoneNumber(),
                 basicInfo.latitude(),
                 basicInfo.longitude(),
                 basicInfo.imageUrl(),
                 menus
         );
         logPotentialBlockSignals(page, placeId, homeHtml, menuHtml, liveHomeSnapshot, analyzeMode);

         if (analyzeMode) {
            if (isBlank(homeHtml)) {
               log.warn("home html 캡처 실패. sourcePlaceId={}, placeUrl={}", placeId, placeUrl);
            }
            log.info(
                    "하이브리드 분석 완료. sourcePlaceId={}, placeName={}, category={}, restaurantAddress={}, phone={}, menuCount={}",
                    result.sourcePlaceId(),
                    result.placeName(),
                    result.category(),
                    result.restaurantAddress(),
                    result.phoneNumber(),
                    result.menus() == null ? 0 : result.menus().size()
            );
         } else {
            log.info(
                    "네이버 플레이스 크롤 완료. sourcePlaceId={}, placeName={}, category={}, restaurantAddress={}, phone={}, menuCount={}",
                    result.sourcePlaceId(),
                    result.placeName(),
                    result.category(),
                    result.restaurantAddress(),
                    result.phoneNumber(),
                    result.menus() == null ? 0 : result.menus().size()
            );
         }

         return result;
      });
   }

   private boolean shouldRetryWithBackoff(NaverPlaceCrawlResult result, int attempt) {
      if (attempt > MAX_RETRIES) {
         return false;
      }
      if (result == null) {
         return true;
      }
      boolean noBasic =
              isBlank(result.placeName())
                      && isBlank(result.category())
                      && isBlank(result.restaurantAddress())
                      && isBlank(result.phoneNumber());
      int menuCount = result.menus() == null ? 0 : result.menus().size();
      return noBasic && menuCount == 0;
   }

   private long retryBackoffMillis(int attempt) {
      // 1차 재시도 전 30초, 2차 재시도 전 60초 대기
      if (attempt == 1) {
         return 30_000;
      }
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
            // 유효 home: 이름 또는 카테고리 힌트가 존재하는 HTML
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

      // 타임아웃 시점에라도 home 이 있으면 그대로 사용한다.
      if (!isBlank(homeHtmlRef.get())) {
         return;
      }

      if (!isBlank(lastObserved)) {
         homeHtmlRef.set(lastObserved);
      }
   }

   private void captureHomeHtmlFromCurrentPageIfMissing(
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
         if (isBlank(html)) {
            html = page.content();
         }
         if (isBlank(html)) {
            return;
         }

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
         if (entryFrame.isEmpty()) {
            return null;
         }
         return entryFrame.get().content();
      } catch (Exception ignored) {
         return null;
      }
   }

   private boolean hasNoBasicInfo(NaverPlaceInfoExtractor.NaverPlaceBasicInfo basicInfo) {
      if (basicInfo == null) {
         return true;
      }
      return isBlank(basicInfo.placeName())
              && isBlank(basicInfo.category())
              && isBlank(basicInfo.restaurantAddress())
              && isBlank(basicInfo.phoneNumber());
   }

   private void logPotentialBlockSignals(
           Page page,
           String placeId,
           String homeHtml,
           String menuHtml,
           String liveHomeSnapshot,
           boolean analyzeMode
   ) {
      if (!analyzeMode) {
         return;
      }

      String currentUrl = safePageUrl(page);
      String title = safePageTitle(page);
      boolean blockedByUrl = containsBlockKeyword(currentUrl);
      boolean blockedByTitle = containsBlockKeyword(title);
      boolean blockedByHome = containsBlockKeyword(homeHtml);
      boolean blockedByMenu = containsBlockKeyword(menuHtml);
      boolean blockedByLive = containsBlockKeyword(liveHomeSnapshot);

      if (blockedByUrl || blockedByTitle || blockedByHome || blockedByMenu || blockedByLive) {
         log.warn(
                 "크롤 차단 징후 감지. sourcePlaceId={}, blockedByUrl={}, blockedByTitle={}, blockedByHome={}, blockedByMenu={}, blockedByLive={}, currentUrl={}, title={}",
                 placeId,
                 blockedByUrl,
                 blockedByTitle,
                 blockedByHome,
                 blockedByMenu,
                 blockedByLive,
                 currentUrl,
                 title
         );
      }
   }

   private String safePageTitle(Page page) {
      try {
         return page.title();
      } catch (Exception ignored) {
         return null;
      }
   }

   private boolean containsBlockKeyword(String text) {
      if (isBlank(text)) {
         return false;
      }
      String lower = text.toLowerCase();
      return lower.contains("자동입력")
              || lower.contains("로봇")
              || lower.contains("비정상")
              || lower.contains("접근 제한")
              || lower.contains("access denied")
              || lower.contains("captcha")
              || lower.contains("unusual traffic");
   }
}
