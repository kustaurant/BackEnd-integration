package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.crawler.RestaurantSync.CrawlGrid;
import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
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
   private static final String FOOD_CATEGORY_TEXT = "음식점";

   private static final String LIST_ITEM_SELECTOR = "li.UEzoS.rTjJo";
   private static final String LIST_SCROLL_CONTAINER_SELECTOR =
           "#_pcmap_list_scroll_container, #pcmap_list_scroll_container, .RvFI";

   private final PlaywrightManager playwrightManager;

   public Set<String> discoverPlaceIds(
           Iterable<CrawlGrid> grids,
           int zoom,
           int maxPlaceIds,
           Consumer<GridDiscoveryProgress> progressListener
   ) {
      Set<String> placeIds = new LinkedHashSet<>();
      int gridIndex = 0;

      for (CrawlGrid grid : grids) {
         gridIndex++;

         if (placeIds.size() >= maxPlaceIds) {
            log.info(
                    "구역 크롤 placeId 수집 최대치 도달. processedGrids={}, discoveredPlaceCount={}",
                    gridIndex - 1,
                    placeIds.size()
            );
            break;
         }

         Set<String> found = discoverPlaceIdsFromGrid(grid, zoom);

         int before = placeIds.size();
         for (String id : found) {
            placeIds.add(id);
            if (placeIds.size() >= maxPlaceIds) {
               break;
            }
         }

         int after = placeIds.size();
         int added = after - before;
         int duplicate = found.size() - added;

         log.info(
                 "그리드 처리 완료. row={}, col={}, found={}, added={}, duplicate={}, total={}",
                 grid.row(),
                 grid.col(),
                 found.size(),
                 added,
                 duplicate,
                 after
         );

         progressListener.accept(new GridDiscoveryProgress(
                 gridIndex,
                 placeIds.size(),
                 grid.row() + "," + grid.col()
         ));
      }

      return placeIds;
   }

   public Set<String> discoverPlaceIdsFromGrid(CrawlGrid grid, int zoom) {
      return playwrightManager.crawl(page -> {
         Set<String> collectedIds = new LinkedHashSet<>();

         String mapUrl = String.format(
                 "https://map.naver.com/p?c=%f,%f,%d,0,0,0,dh",
                 grid.centerLng(),
                 grid.centerLat(),
                 zoom
         );

         try {
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
            page.waitForTimeout(3_000);

            moveMapToGridCenter(page, grid, zoom);
            page.waitForTimeout(1_500);

            clickFoodCategoryButton(page);

            Frame searchFrame = waitForSearchIframe(page, grid);
            waitForInitialListReady(page, searchFrame);
            collectPlaceIdsByClickingList(page, searchFrame, collectedIds);

            log.info(
                    "그리드 목록 placeId 수집 완료. row={}, col={}, collectedCount={}, currentUrl={}",
                    grid.row(),
                    grid.col(),
                    collectedIds.size(),
                    safePageUrl(page)
            );
         } catch (Exception e) {
            log.warn(
                    "그리드 목록 placeId 수집 실패. grid({},{}), reason={}, currentUrl={}",
                    grid.row(),
                    grid.col(),
                    e.getMessage(),
                    safePageUrl(page),
                    e
            );
            dumpDebugInfo(page, grid, "discover-failed");
         }

         return collectedIds;
      });
   }

   private void collectPlaceIdsByClickingList(Page page, Frame searchFrame, Set<String> collector) {
      int lastProcessedIndex = 0;
      int stableRounds = 0;

      for (int round = 0; round < 20; round++) {
         Locator items = searchFrame.locator(LIST_ITEM_SELECTOR);
         int currentCount = items.count();

         log.debug("검색 리스트 상태. round={}, currentCount={}, lastProcessedIndex={}",
                 round, currentCount, lastProcessedIndex);

         if (currentCount > lastProcessedIndex) {
            for (int i = lastProcessedIndex; i < currentCount; i++) {
               try {
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

                  page.waitForTimeout(150);
               } catch (Exception e) {
                  log.warn("목록 아이템 클릭 실패. index={}, reason={}", i, e.getMessage());
               }
            }

            lastProcessedIndex = currentCount;
            stableRounds = 0;
         } else {
            stableRounds++;
         }

         if (stableRounds >= 3) {
            log.debug("검색 리스트 안정화 감지. stableRounds={}", stableRounds);
            break;
         }

         boolean scrolled = scrollSearchListStepByStep(searchFrame);
         if (!scrolled) {
            stableRounds++;
         }

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
      page.waitForTimeout(1000);

      for (int i = 0; i < 20; i++) {
         int count = searchFrame.locator(LIST_ITEM_SELECTOR).count();
         if (count >= 1) {
            log.debug("초기 검색 리스트 로딩 완료. itemCount={}", count);
            return;
         }
         page.waitForTimeout(300);
      }

      log.warn("초기 검색 리스트 로딩 지연. itemCount={}", searchFrame.locator(LIST_ITEM_SELECTOR).count());
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

   private Optional<String> waitForEntryIframePlaceIdChange(Page page, Optional<String> previousId) {
      for (int i = 0; i < 12; i++) {
         Optional<String> currentId = extractPlaceIdFromEntryIframe(page);

         if (currentId.isPresent()) {
            if (previousId.isEmpty() || !previousId.get().equals(currentId.get())) {
               return currentId;
            }
         }

         page.waitForTimeout(200);
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

   private Frame waitForSearchIframe(Page page, CrawlGrid grid) {
      for (int i = 0; i < 50; i++) {
         Frame byName = page.frame(SEARCH_IFRAME_NAME);
         if (byName != null) {
            log.debug("search iframe 발견(name). attempt={}, frameUrl={}", i, safeFrameUrl(byName));
            return byName;
         }

         for (Frame frame : page.frames()) {
            String frameName = safeFrameName(frame);
            String frameUrl = safeFrameUrl(frame);

            if (SEARCH_IFRAME_NAME.equals(frameName)) {
               log.debug("search iframe 발견(frame list name). attempt={}, frameUrl={}", i, frameUrl);
               return frame;
            }

            if (frameUrl != null && frameUrl.contains("/search")) {
               log.debug("search iframe 발견(frame url). attempt={}, frameName={}, frameUrl={}", i, frameName, frameUrl);
               return frame;
            }
         }

         page.waitForTimeout(200);
      }

      log.warn("search iframe 탐색 실패. row={}, col={}, currentUrl={}, title={}",
              grid.row(), grid.col(), safePageUrl(page), safePageTitle(page));

      for (Frame frame : page.frames()) {
         log.warn("frame dump. name={}, url={}", safeFrameName(frame), safeFrameUrl(frame));
      }

      dumpDebugInfo(page, grid, "search-iframe-not-found");
      throw new IllegalStateException("search iframe not found");
   }

   private void clickFoodCategoryButton(Page page) {
      try {
         Locator foodButton = page.getByRole(
                 AriaRole.BUTTON,
                 new Page.GetByRoleOptions().setName(FOOD_CATEGORY_TEXT)
         );
         if (foodButton.count() > 0) {
            foodButton.first().click(new Locator.ClickOptions().setTimeout(5_000));
            log.debug("음식점 버튼 클릭 성공(getByRole)");
            return;
         }
      } catch (Exception ignored) {
      }

      try {
         Locator foodButton = page.locator("text=" + FOOD_CATEGORY_TEXT);
         if (foodButton.count() > 0) {
            foodButton.first().click(new Locator.ClickOptions().setTimeout(5_000));
            log.debug("음식점 버튼 클릭 성공(text locator)");
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
            log.debug("음식점 버튼 클릭 성공(js evaluate)");
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

         if (!centeredUrl.equals(safePageUrl(page))) {
            page.navigate(centeredUrl, new Page.NavigateOptions().setTimeout(10_000));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(1200);
         }
      } catch (Exception e) {
         log.debug("그리드 중심 이동 중 예외 무시. reason={}", e.getMessage());
      }
   }

   private void dumpDebugInfo(Page page, CrawlGrid grid, String suffix) {
      try {
         log.warn("debug page info. row={}, col={}, url={}, title={}",
                 grid.row(), grid.col(), safePageUrl(page), safePageTitle(page));

         String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
         String fileName = String.format(
                 "naver-map-%s-r%s-c%s-%s.png",
                 suffix,
                 grid.row(),
                 grid.col(),
                 timestamp
         );

         page.screenshot(new Page.ScreenshotOptions()
                 .setPath(Paths.get(fileName))
                 .setFullPage(true));

         log.warn("debug screenshot saved. path={}", fileName);
      } catch (Exception e) {
         log.warn("debug 정보 저장 실패. reason={}", e.getMessage());
      }
   }

   private String safePageUrl(Page page) {
      try {
         return page.url();
      } catch (Exception e) {
         return null;
      }
   }

   private String safePageTitle(Page page) {
      try {
         return page.title();
      } catch (Exception e) {
         return null;
      }
   }

   private String safeFrameName(Frame frame) {
      try {
         return frame.name();
      } catch (Exception e) {
         return null;
      }
   }

   private String safeFrameUrl(Frame frame) {
      try {
         return frame.url();
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

   public record GridDiscoveryProgress(
           int processedGridCount,
           int discoveredPlaceCount,
           String currentGrid
   ) {
   }
}