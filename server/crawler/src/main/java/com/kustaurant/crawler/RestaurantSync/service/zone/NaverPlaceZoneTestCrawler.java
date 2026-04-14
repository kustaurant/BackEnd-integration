package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.crawler.RestaurantSync.CrawlGrid;
import com.kustaurant.crawler.RestaurantSync.GridGenerator;
import com.kustaurant.crawler.RestaurantSync.service.single.NaverPlaceCrawler;
import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.kustaurant.map.CoordinateV2;
import com.kustaurant.map.MapConstantsV2;
import com.kustaurant.map.ZonePolygon;
import com.kustaurant.map.utils.PolygonUtils;
import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlResult;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
public class NaverPlaceZoneTestCrawler {

   private static final double DEFAULT_LAT_STEP = 0.0018;
   private static final double DEFAULT_LNG_STEP = 0.0022;
   private static final int DEFAULT_ZOOM = 19;
   private static final int MAX_CRAWL_COUNT = 10;

   private static final String SEARCH_IFRAME_NAME = "searchIframe";
   private static final String ENTRY_IFRAME_SELECTOR = "iframe#entryIframe";
   private static final Pattern ENTRY_IFRAME_PLACE_ID_PATTERN = Pattern.compile("/place/(\\d+)");
   private static final String FOOD_CATEGORY_TEXT = "\uC74C\uC2DD\uC810";

   private static final String LIST_ITEM_SELECTOR = "li.UEzoS.rTjJo";
   private static final String LIST_SCROLL_CONTAINER_SELECTOR = "#_pcmap_list_scroll_container, #pcmap_list_scroll_container, .RvFI";

   private final PlaywrightManager playwrightManager;
   private final NaverPlaceCrawler naverPlaceCrawler;

   public NaverPlaceZoneCrawlResult test(CrawlScopeType crawlScope) {
      ZonePolygon zone = findZonePolygon(crawlScope)
              .orElseThrow(() -> new IllegalArgumentException("Unsupported crawl scope: " + crawlScope));

      List<CrawlGrid> grids = GridGenerator.generate(zone, DEFAULT_LAT_STEP, DEFAULT_LNG_STEP);
      if (grids.isEmpty()) {
         return new NaverPlaceZoneCrawlResult(crawlScope, 0, 0, List.of());
      }

      CrawlGrid firstGrid = grids.get(0);

      return playwrightManager.crawl(page -> {
         Set<String> collectedIds = new LinkedHashSet<>();
         List<NaverPlaceCrawlResult> results = new ArrayList<>();

         String mapUrl = String.format(
                 "https://map.naver.com/p?c=%f,%f,%d,0,0,0,dh",
                 firstGrid.centerLng(),
                 firstGrid.centerLat(),
                 DEFAULT_ZOOM
         );

         try {
            log.info(
                    "단일 그리드 구역 크롤 시작. row={}, col={}, centerLat={}, centerLng={}, mapUrl={}",
                    firstGrid.row(),
                    firstGrid.col(),
                    firstGrid.centerLat(),
                    firstGrid.centerLng(),
                    mapUrl
            );

            page.navigate(mapUrl, new Page.NavigateOptions().setTimeout(30_000));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(2_000);
            log.info("단일 그리드 지도 로드 완료. currentUrl={}", page.url());

            moveMapToGridCenter(page, firstGrid);
            page.waitForTimeout(1_000);
            log.info("단일 그리드 지도 중심 이동 완료. row={}, col={}", firstGrid.row(), firstGrid.col());

            clickFoodCategoryButton(page);
            page.waitForTimeout(2_500);
            log.info("단일 그리드 음식점 카테고리 클릭 완료.");

            waitForSearchIframe(page);
            log.info("단일 그리드 search iframe 준비 완료.");
            collectPlaceIdsByClickingList(page, collectedIds);
            log.info("단일 그리드 placeId 수집 완료. discoveredCount={}", collectedIds.size());

            int crawled = 0;
            for (String placeId : collectedIds) {
               if (crawled >= MAX_CRAWL_COUNT) {
                  log.info("단일 그리드 상세 크롤 최대치 도달. limit={}", MAX_CRAWL_COUNT);
                  break;
               }

               String placeUrl = "https://map.naver.com/p/entry/place/" + placeId;
               log.info("단일 그리드 상세 크롤 시작. placeId={}, placeUrl={}", placeId, placeUrl);
               try {
                  NaverPlaceCrawlResult result = naverPlaceCrawler.analyze(placeUrl);
                  if (!isMeaningfulResult(result)) {
                     log.info(
                             "테스트 단건 결과 스킵(무의미 결과). scope={}, placeId={}, placeName={}, lat={}, lng={}, address={}",
                             crawlScope,
                             placeId,
                             result == null ? null : result.placeName(),
                             result == null ? null : result.latitude(),
                             result == null ? null : result.longitude(),
                             result == null ? null : result.restaurantAddress()
                     );
                     continue;
                  }

                  if (!isPlaceInsideZone(result, zone)) {
                     log.info(
                             "테스트 단건 결과 스킵(구역 밖). scope={}, placeId={}, placeName={}, lat={}, lng={}, address={}",
                             crawlScope,
                             result.sourcePlaceId(),
                             result.placeName(),
                             result.latitude(),
                             result.longitude(),
                             result.restaurantAddress()
                     );
                     continue;
                  }

                  results.add(result);
                  crawled++;
                  log.info(
                          "단일 그리드 상세 크롤 결과. placeId={}, placeName={}, category={}, address={}, phone={}, menuCount={}",
                          placeId,
                          result.placeName(),
                          result.category(),
                          result.restaurantAddress(),
                          result.phoneNumber(),
                          result.menus() == null ? 0 : result.menus().size()
                  );
                  log.info(
                          "단일 그리드 상세 크롤 성공. placeId={}, placeName={}, crawledCount={}",
                          placeId,
                          result.placeName(),
                          crawled
                  );
               } catch (Exception e) {
                  log.warn("단일 그리드 상세 크롤 실패. placeId={}", placeId, e);
               } finally {
                  long delayMs = ThreadLocalRandom.current().nextLong(3_000, 5_001);
                  log.info("다음 placeId 크롤 전 랜덤 대기. placeId={}, waitMs={}", placeId, delayMs);
                  page.waitForTimeout(delayMs);
               }
            }
            log.info("단일 그리드 크롤 종료. discoveredCount={}, successCount={}", collectedIds.size(), results.size());

         } catch (Exception e) {
            log.warn("단일 그리드 구역 크롤 실패. reason={}", e.getMessage(), e);
         }

         return new NaverPlaceZoneCrawlResult(
                 crawlScope,
                 collectedIds.size(),
                 results.size(),
                 results
         );
      });
   }

   private void collectPlaceIdsByClickingList(Page page, Set<String> collector) {
      Frame searchFrame = getSearchFrameOrThrow(page);
      waitForInitialListReady(page, searchFrame);

      int lastProcessedIndex = 0;
      int stableRounds = 0;

      for (int round = 0; round < 20; round++) {
         Locator items = searchFrame.locator(LIST_ITEM_SELECTOR);
         int currentCount = items.count();
         log.info(
                 "목록 라운드 시작. round={}, currentCount={}, lastProcessedIndex={}, collectedCount={}",
                 round,
                 currentCount,
                 lastProcessedIndex,
                 collector.size()
         );

         if (currentCount > lastProcessedIndex) {
            for (int i = lastProcessedIndex; i < currentCount; i++) {
               try {
                  Locator item = items.nth(i);
                  String placeName = extractPlaceName(item);
                  if (placeName.isBlank()) {
                     log.warn("목록 아이템 이름 비어있어 스킵. index={}", i);
                     continue;
                  }

                  Optional<String> before = extractPlaceIdFromEntryIframe(page);
                  log.info(
                          "목록 아이템 클릭 시작. index={}, beforeId={}, placeName={}",
                          i,
                          before.orElse(null),
                          placeName
                  );

                  clickPrimaryPlaceLink(item);
                  Optional<String> after = waitForEntryIframePlaceIdChange(page, before);
                  if (after.isPresent()) {
                     collector.add(after.get());
                     log.info(
                             "목록 아이템 클릭 성공. index={}, placeId={}, placeName={}, entryIframeSrc={}",
                             i,
                             after.get(),
                             placeName,
                             safeEntryIframeSrc(page)
                     );
                  } else {
                     log.warn(
                             "목록 아이템 클릭 후 변화 없음. index={}, beforeId={}, placeName={}, entryIframeSrc={}",
                             i,
                             before.orElse(null),
                             placeName,
                             safeEntryIframeSrc(page)
                     );
                  }
                  page.waitForTimeout(120);
               } catch (Exception e) {
                  log.warn("목록 아이템 클릭 실패. index={}, reason={}", i, e.getMessage(), e);
               }
            }

            lastProcessedIndex = currentCount;
            stableRounds = 0;
         } else {
            stableRounds++;
            log.info(
                    "스크롤 후 신규 아이템 없음. round={}, stableRounds={}, currentCount={}, lastProcessedIndex={}",
                    round,
                    stableRounds,
                    currentCount,
                    lastProcessedIndex
            );
         }

         if (stableRounds >= 3) {
            log.info(
                    "목록 수집 안정화로 종료. round={}, lastProcessedIndex={}, collectedCount={}",
                    round,
                    lastProcessedIndex,
                    collector.size()
            );
            break;
         }

         boolean scrolled = scrollSearchListStepByStep(searchFrame);
         page.waitForTimeout(700);
         log.info(
                 "목록 라운드 스크롤. round={}, scrolled={}, currentCount={}, lastProcessedIndex={}",
                 round,
                 scrolled,
                 currentCount,
                 lastProcessedIndex
         );
      }
   }

   private Frame getSearchFrameOrThrow(Page page) {
      Frame frame = page.frame(SEARCH_IFRAME_NAME);
      if (frame == null) {
         throw new IllegalStateException("search iframe not found");
      }
      return frame;
   }

   private void waitForInitialListReady(Page page, Frame searchFrame) {
      page.waitForTimeout(1500);
      for (int i = 0; i < 10; i++) {
         int count = searchFrame.locator(LIST_ITEM_SELECTOR).count();
         if (count >= 5) {
            log.info("초기 목록 준비 완료. visibleCount={}", count);
            return;
         }
         page.waitForTimeout(300);
      }
      log.info("초기 목록 대기 종료(표시 개수 부족). visibleCount={}", searchFrame.locator(LIST_ITEM_SELECTOR).count());
   }

   private void clickPrimaryPlaceLink(Locator item) {
      Locator primaryLink = item.locator("a.YTJkH.CtW3e").first();
      if (primaryLink.count() == 0) {
         throw new IllegalStateException("primary place link not found");
      }
      primaryLink.click(new Locator.ClickOptions().setTimeout(5_000));
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
      for (int i = 0; i < 8; i++) {
         Optional<String> currentId = extractPlaceIdFromEntryIframe(page);
         if (currentId.isPresent() && (previousId.isEmpty() || !previousId.get().equals(currentId.get()))) {
            return currentId;
         }
         page.waitForTimeout(150);
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
      for (int i = 0; i < 35; i++) {
         if (page.frame(SEARCH_IFRAME_NAME) != null) {
            return;
         }
         page.waitForTimeout(200);
      }
      throw new IllegalStateException("search iframe not found");
   }

   private void moveMapToGridCenter(Page page, CrawlGrid grid) {
      try {
         String centeredUrl = String.format(
                 "https://map.naver.com/p?c=%f,%f,%d,0,0,0,dh",
                 grid.centerLng(),
                 grid.centerLat(),
                 DEFAULT_ZOOM
         );
         page.navigate(centeredUrl, new Page.NavigateOptions().setTimeout(10_000));
         page.waitForTimeout(1200);
      } catch (Exception ignored) {
      }
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

   private Optional<ZonePolygon> findZonePolygon(CrawlScopeType crawlScope) {
      return MapConstantsV2.ZONES.stream()
              .filter(zone -> CrawlScopeType.fromZoneType(zone.zoneType()) == crawlScope)
              .findFirst();
   }

   private boolean isMeaningfulResult(NaverPlaceCrawlResult result) {
      return result != null
              && !isBlank(result.sourcePlaceId())
              && !isBlank(result.placeName())
              && result.latitude() != null
              && result.longitude() != null;
   }

   private boolean isPlaceInsideZone(NaverPlaceCrawlResult result, ZonePolygon zone) {
      CoordinateV2 point = new CoordinateV2(result.latitude(), result.longitude());
      return PolygonUtils.isPointInsidePolygon(point, zone.coordinates());
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

   private String safeEntryIframeSrc(Page page) {
      try {
         Locator entryIframe = page.locator("iframe#entryIframe");
         if (entryIframe.count() == 0) {
            return null;
         }
         return entryIframe.first().getAttribute("src");
      } catch (Exception e) {
         return null;
      }
   }

   private boolean isBlank(String value) {
      return value == null || value.isBlank();
   }
}
