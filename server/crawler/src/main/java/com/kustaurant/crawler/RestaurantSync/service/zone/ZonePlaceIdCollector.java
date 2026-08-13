package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.crawler.RestaurantSync.CrawlGrid;
import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZonePlaceIdCollector {
   private static final Pattern ENTRY_IFRAME_PLACE_ID_PATTERN = Pattern.compile("/place/(\\d+)");
   private static final String SEARCH_IFRAME_NAME = "searchIframe";
   private static final String ENTRY_IFRAME_SELECTOR = "iframe#entryIframe";
   private static final String FOOD_CATEGORY_TEXT = "\uC74C\uC2DD\uC810";

   private static final String LIST_ITEM_SELECTOR = "li.UEzoS.rTjJo";
   private static final String LIST_SCROLL_CONTAINER_SELECTOR = "#_pcmap_list_scroll_container, #pcmap_list_scroll_container, .RvFI";
   private static final int GRID_DISCOVERY_MAX_ATTEMPTS = 3;
   private static final long GRID_RETRY_BASE_DELAY_MS = 1_500L;
   private static final long GRID_RETRY_JITTER_MAX_MS = 1_000L;

   private final PlaywrightManager playwrightManager;
   private final NaverCaptchaDetector captchaDetector;

   public Set<String> discoverPlaceIdsFromGrid(CrawlGrid grid, int zoom) {
      for (int attempt = 1; attempt <= GRID_DISCOVERY_MAX_ATTEMPTS; attempt++) {
         try {
            return playwrightManager.crawl(page -> discoverPlaceIdsFromGridOnce(page, grid, zoom));
         } catch (Exception e) {
            boolean captchaDetected = isCaptchaFailure(e);
            boolean hasNextAttempt = attempt < GRID_DISCOVERY_MAX_ATTEMPTS;
            log.warn(
                    "그리드 목록 placeId 수집 실패. grid({},{}), attempt={}/{}, captcha={}, willRetry={}, reason={}",
                    grid.row(),
                    grid.col(),
                    attempt,
                    GRID_DISCOVERY_MAX_ATTEMPTS,
                    captchaDetected,
                    !captchaDetected && hasNextAttempt,
                    e.getMessage(),
                    e
            );

            if (captchaDetected) {
               log.warn(
                       "네이버 보안 인증 감지. 자동 재시도 없이 작업을 일시정지합니다. grid=({}, {})",
                       grid.row(), grid.col()
               );
               throw new NaverCaptchaRequiredException(grid.row(), grid.col());
            }

            if (!hasNextAttempt) {
               throw new NaverGridDiscoveryException(grid.row(), grid.col(), e);
            }

            sleepMillis(retryDelayMillis(attempt));
         }
      }

      throw new NaverGridDiscoveryException(
              grid.row(),
              grid.col(),
              new IllegalStateException("그리드 재시도 횟수를 소진했습니다.")
      );
   }

   private Set<String> discoverPlaceIdsFromGridOnce(Page page, CrawlGrid grid, int zoom) {
      Set<String> collectedIds = new LinkedHashSet<>();

      String mapUrl = String.format(
              "https://map.naver.com/p?c=%f,%f,%d,0,0,0,dh",
              grid.centerLng(),
              grid.centerLat(),
              zoom
      );

      log.info(
              "구역 크롤 그리드 이동 시작. row={}, col={}, centerLat={}, centerLng={}, mapUrl={}",
              grid.row(),
              grid.col(),
              grid.centerLat(),
              grid.centerLng(),
              mapUrl
      );

      page.navigate(mapUrl, new Page.NavigateOptions().setTimeout(30_000));
      page.waitForLoadState(LoadState.DOMCONTENTLOADED);
      page.waitForTimeout(2_000);
      throwIfCaptchaDetected(page, grid);

      moveMapToGridCenter(page, grid, zoom);
      page.waitForTimeout(1_000);
      throwIfCaptchaDetected(page, grid);

      clickFoodCategoryButton(page);
      page.waitForTimeout(2_500);
      throwIfCaptchaDetected(page, grid);

      waitForSearchIframe(page);
      collectPlaceIdsByClickingList(page, collectedIds, grid);
      throwIfCaptchaDetected(page, grid);

      log.info(
              "그리드 목록 placeId 수집 완료. row={}, col={}, collectedCount={}, currentUrl={}",
              grid.row(),
              grid.col(),
              collectedIds.size(),
              safePageUrl(page)
      );

      return collectedIds;
   }

   private void collectPlaceIdsByClickingList(Page page, Set<String> collector, CrawlGrid grid) {
      Frame searchFrame = getSearchFrameOrThrow(page);
      waitForInitialListReady(page, searchFrame);

      int lastProcessedIndex = 0;
      int stableRounds = 0;

      for (int round = 0; round < 30; round++) {
         throwIfCaptchaDetected(page, grid);
         Locator items = searchFrame.locator(LIST_ITEM_SELECTOR);
         int currentCount = items.count();

         if (currentCount > lastProcessedIndex) {
            for (int i = lastProcessedIndex; i < currentCount; i++) {
               try {
                  throwIfCaptchaDetected(page, grid);
                  Locator item = items.nth(i);

                  String placeName = extractPlaceName(item);
                  if (placeName.isBlank()) {
                     continue;
                  }

                  Optional<String> before = extractPlaceIdFromEntryIframe(page);
                  clickPrimaryPlaceLink(item);
                  Optional<String> after = waitForEntryIframePlaceIdChange(page, before);

                  after.ifPresent(placeId -> {
                     boolean added = collector.add(placeId);
                     log.info(
                             "구역 크롤 ID 수집. placeId={}, placeName={}, isNew={}",
                             placeId,
                             placeName,
                             added
                     );
                  });
                  page.waitForTimeout(120);
               } catch (Exception e) {
                  if (isCaptchaFailure(e) || captchaDetector.isDetected(page)) {
                     throw new NaverCaptchaRequiredException(grid.row(), grid.col());
                  }
                  log.warn("목록 아이템 클릭 실패. index={}, reason={}", i, e.getMessage());
               }
            }

            lastProcessedIndex = currentCount;
            stableRounds = 0;
         } else {
            stableRounds++;
         }

         if (stableRounds >= 3 && isSearchListAtBottom(searchFrame)) {
            break;
         }

         scrollSearchListStepByStep(searchFrame);
         page.waitForTimeout(700);
      }
   }

   private void clickPrimaryPlaceLink(Locator item) {
      Locator primaryLink = item.locator("a.YTJkH.CtW3e").first();
      if (primaryLink.count() == 0) {
         throw new IllegalStateException("primary place link not found");
      }
      primaryLink.click(new Locator.ClickOptions().setTimeout(5_000));
   }

   private void waitForInitialListReady(Page page, Frame searchFrame) {
      page.waitForTimeout(1_500);

      for (int i = 0; i < 10; i++) {
         int count = searchFrame.locator(LIST_ITEM_SELECTOR).count();
         if (count >= 5) {
            return;
         }
         page.waitForTimeout(300);
      }
   }

   private boolean scrollSearchListStepByStep(Frame searchFrame) {
      try {
         return Boolean.TRUE.equals(searchFrame.evaluate("""
            (selector) => {
                const el = document.querySelector(selector);
                if (!el) return false;

                const beforeTop = el.scrollTop;
                const step = Math.max(300, Math.floor(el.clientHeight * 0.8));

                el.scrollTop = beforeTop + step;

                return el.scrollTop !== beforeTop;
            }
        """, LIST_SCROLL_CONTAINER_SELECTOR));
      } catch (Exception e) {
         log.warn("검색 목록 스크롤 실패. reason={}", e.getMessage());
         return false;
      }
   }

   private boolean isSearchListAtBottom(Frame searchFrame) {
      try {
         return Boolean.TRUE.equals(searchFrame.evaluate("""
            (selector) => {
                const el = document.querySelector(selector);
                if (!el) return false;

                return el.scrollTop + el.clientHeight >= el.scrollHeight - 2;
            }
        """, LIST_SCROLL_CONTAINER_SELECTOR));
      } catch (Exception e) {
         log.warn("검색 목록 바닥 판별 실패. reason={}", e.getMessage());
         return false;
      }
   }

   private Optional<String> waitForEntryIframePlaceIdChange(Page page, Optional<String> previousId) {
      for (int i = 0; i < 4; i++) {
         Optional<String> currentId = extractPlaceIdFromEntryIframe(page);

         if (currentId.isPresent()) {
            if (previousId.isEmpty() || !previousId.get().equals(currentId.get())) {
               return currentId;
            }
         }

         page.waitForTimeout(300);
      }

      return Optional.empty();
   }

   private Optional<String> extractPlaceIdFromEntryIframe(Page page) {
      try {
         Locator entryIframe = page.locator(ENTRY_IFRAME_SELECTOR);
         if (entryIframe.count() == 0) {
            return Optional.empty();
         }

         String src = entryIframe.first().getAttribute("src");
         if (src == null || src.isBlank()) {
            return Optional.empty();
         }

         Matcher matcher = ENTRY_IFRAME_PLACE_ID_PATTERN.matcher(src);
         if (matcher.find()) {
            return Optional.of(matcher.group(1));
         }

         return Optional.empty();
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   private void waitForSearchIframe(Page page) {
      for (int i = 0; i < 10; i++) {
         Frame frame = page.frame(SEARCH_IFRAME_NAME);
         if (frame != null) {
            return;
         }
         page.waitForTimeout(1_000);
      }

      throw new IllegalStateException("search iframe not found");
   }

   private Frame getSearchFrameOrThrow(Page page) {
      Frame frame = page.frame(SEARCH_IFRAME_NAME);
      if (frame == null) {
         throw new IllegalStateException("search iframe not found");
      }
      return frame;
   }

   private void clickFoodCategoryButton(Page page) {
      try {
         Locator foodButton = page.getByRole(
                 AriaRole.BUTTON,
                 new Page.GetByRoleOptions().setName(FOOD_CATEGORY_TEXT)
         );
         if (foodButton.count() > 0) {
            foodButton.first().click(new Locator.ClickOptions().setTimeout(3_000));
            return;
         }
      } catch (Exception ignored) {
      }

      try {
         Locator foodButton = page.locator("text=" + FOOD_CATEGORY_TEXT);
         if (foodButton.count() > 0) {
            foodButton.first().click(new Locator.ClickOptions().setTimeout(3_000));
            return;
         }
      } catch (Exception ignored) {
      }

      try {
         Object raw = page.evaluate("""
                (targetText) => {
                    const elements = Array.from(document.querySelectorAll('button, a, span, div'));
                    const target = elements.find(el => (el.innerText || '').trim() === targetText);
                    if (!target) return false;
                    target.click();
                    return true;
                }
            """, FOOD_CATEGORY_TEXT);

         if (Boolean.TRUE.equals(raw)) {
            return;
         }
      } catch (Exception ignored) {
      }

      throw new IllegalStateException("food category button not found");
   }

   private void moveMapToGridCenter(Page page, CrawlGrid grid, int zoom) {
      try {
         String centeredUrl = String.format(
                 "https://map.naver.com/p?c=%f,%f,%d,0,0,0,dh",
                 grid.centerLng(),
                 grid.centerLat(),
                 zoom
         );
         page.navigate(centeredUrl, new Page.NavigateOptions().setTimeout(10_000));
         page.waitForTimeout(1_200);
      } catch (Exception ignored) {
      }
   }

   private String safePageUrl(Page page) {
      try {
         return page.url();
      } catch (Exception e) {
         return null;
      }
   }

   private String extractPlaceName(Locator item) {
      try {
         Locator nameEl = item.locator("a.YTJkH.CtW3e").first();
         String text = nameEl.innerText().trim();
         return text.split("\\R", 2)[0].trim();
      } catch (Exception e) {
         return "";
      }
   }

   private void throwIfCaptchaDetected(Page page, CrawlGrid grid) {
      if (captchaDetector.isDetected(page)) {
         throw new NaverCaptchaRequiredException(grid.row(), grid.col());
      }
   }

   private boolean isCaptchaFailure(Throwable error) {
      Throwable current = error;
      while (current != null) {
         if (current instanceof NaverCaptchaRequiredException) {
            return true;
         }
         String message = current.getMessage();
         if (message != null && (message.contains("wtm-captcha-root") || message.contains("보안 인증 필요"))) {
            return true;
         }
         current = current.getCause();
      }
      return false;
   }

   private long retryDelayMillis(int attempt) {
      long base = GRID_RETRY_BASE_DELAY_MS * attempt;
      long jitter = ThreadLocalRandom.current().nextLong(0, GRID_RETRY_JITTER_MAX_MS + 1);
      return base + jitter;
   }

   private void sleepMillis(long millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

}
