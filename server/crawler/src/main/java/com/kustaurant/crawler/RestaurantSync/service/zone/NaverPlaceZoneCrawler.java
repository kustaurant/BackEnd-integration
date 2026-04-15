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
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverPlaceZoneCrawler {
   private static final double DEFAULT_LAT_STEP = 0.0018;
   private static final double DEFAULT_LNG_STEP = 0.0022;
   private static final int DEFAULT_ZOOM = 19;
   private static final int MAX_PLACE_IDS = Integer.MAX_VALUE;

   private static final Pattern ENTRY_IFRAME_PLACE_ID_PATTERN = Pattern.compile("/place/(\\d+)");
   private static final String SEARCH_IFRAME_NAME = "searchIframe";
   private static final String ENTRY_IFRAME_SELECTOR = "iframe#entryIframe";

   private static final String LIST_ITEM_SELECTOR = "li.UEzoS.rTjJo";
   private static final String LIST_PRIMARY_LINK_SELECTOR = "a.YTJkH.CtW3e";
   private static final String LIST_SCROLL_CONTAINER_SELECTOR = "#_pcmap_list_scroll_container, #pcmap_list_scroll_container, .RvFI";
   private static final String LIST_ITEM_BUTTON_SELECTOR = "a.vEZDX.hZI_k[role='button']";


   private final PlaywrightManager playwrightManager;
   private final NaverPlaceCrawler naverPlaceCrawler;

   public NaverPlaceZoneCrawlResult crawlByScope(CrawlScopeType crawlScope) {
      return crawlByScope(crawlScope, progress -> {});
   }

   public NaverPlaceZoneCrawlResult crawlByScope(
           CrawlScopeType crawlScope,
           Consumer<ZoneCrawlProgress> progressListener
   ) {
      ZonePolygon zone = findZonePolygon(crawlScope)
              .orElseThrow(() -> new IllegalArgumentException("Unsupported crawl scope: " + crawlScope));

      List<CrawlGrid> grids = GridGenerator.generate(zone, DEFAULT_LAT_STEP, DEFAULT_LNG_STEP);

      log.info(
              "구역 크롤 시작. scope={}, zoneType={}, gridCount={}, latStep={}, lngStep={}, zoom={}, maxPlaceIds={}",
              crawlScope,
              zone.zoneType(),
              grids.size(),
              DEFAULT_LAT_STEP,
              DEFAULT_LNG_STEP,
              DEFAULT_ZOOM,
              "UNLIMITED"
      );

      Set<String> placeIds = discoverPlaceIds(grids, progress -> {
         progressListener.accept(new ZoneCrawlProgress(
                 "DISCOVERING",
                 grids.size(),
                 progress.processedGridCount(),
                 progress.discoveredPlaceCount(),
                 0,
                 0,
                 0,
                 progress.currentGrid(),
                 null
         ));
      });

      log.info(" === 구역 크롤 placeId 수집 완료. scope={}, discoveredPlaceCount={}", crawlScope, placeIds.size());

      long preCrawlWaitMs = 60_000L;
      log.info("placeId 수집 완료 후 단건 크롤 시작 전 대기. scope={}, waitMs={}", crawlScope, preCrawlWaitMs);
      try {
         Thread.sleep(preCrawlWaitMs);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         log.warn("단건 크롤 시작 전 대기 중 인터럽트 발생. scope={}", crawlScope);
      }

      List<NaverPlaceCrawlResult> results = new ArrayList<>();
      int crawlAttempt = 0;

      for (String placeId : placeIds) {
         crawlAttempt++;

         progressListener.accept(new ZoneCrawlProgress(
                 "CRAWLING",
                 grids.size(),
                 grids.size(),
                 placeIds.size(),
                 placeIds.size(),
                 crawlAttempt,
                 results.size(),
                 null,
                 placeId
         ));

         String placeUrl = "https://map.naver.com/p/entry/place/" + placeId;

         try {
            NaverPlaceCrawlResult result = naverPlaceCrawler.crawl(placeUrl);

            if (!isMeaningfulResult(result)) {
               log.info(
                       "유효하지 않은 결과 스킵. scope={}, placeId={}, placeName={}, address={}",
                       crawlScope,
                       placeId,
                       result == null ? null : result.placeName(),
                       result == null ? null : result.restaurantAddress()
               );
               continue;
            }

            if (!isPlaceInsideZone(result, zone)) {
               log.info(
                       "구역 밖 결과 스킵. scope={}, placeId={}, placeName={}, lat={}, lng={}, address={}",
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

            log.info(
                    "구역 크롤 상세 결과 채택. scope={}, placeId={}, placeName={}, acceptedCount={}",
                    crawlScope,
                    result.sourcePlaceId(),
                    result.placeName(),
                    results.size()
            );

         } catch (Exception e) {
            log.warn("구역 크롤 상세 수집 실패. placeId={}, scope={}", placeId, crawlScope, e);
         }

         progressListener.accept(new ZoneCrawlProgress(
                 "CRAWLING",
                 grids.size(),
                 grids.size(),
                 placeIds.size(),
                 placeIds.size(),
                 crawlAttempt,
                 results.size(),
                 null,
                 placeId
         ));
      }

      progressListener.accept(new ZoneCrawlProgress(
              "COMPLETED",
              grids.size(),
              grids.size(),
              placeIds.size(),
              placeIds.size(),
              placeIds.size(),
              results.size(),
              null,
              null
      ));

      log.info(
              "구역 크롤 종료. scope={}, discoveredPlaceCount={}, crawledSuccessCount={}",
              crawlScope,
              placeIds.size(),
              results.size()
      );

      return new NaverPlaceZoneCrawlResult(
              crawlScope,
              placeIds.size(),
              results.size(),
              results
      );
   }

   private Set<String> discoverPlaceIds(
           List<CrawlGrid> grids,
           Consumer<GridDiscoveryProgress> progressListener
   ) {
      Set<String> placeIds = new LinkedHashSet<>();
      int gridIndex = 0;

      for (CrawlGrid grid : grids) {
         gridIndex++;

         if (placeIds.size() >= MAX_PLACE_IDS) {
            log.info(
                    "구역 크롤 placeId 수집 최대치 도달. processedGrids={}, discoveredPlaceCount={}",
                    gridIndex - 1,
                    placeIds.size()
            );
            break;
         }

         Set<String> found = discoverPlaceIdsFromGrid(grid);

         int before = placeIds.size();
         for (String id : found) {
            placeIds.add(id);
            if (placeIds.size() >= MAX_PLACE_IDS) {
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

   private Set<String> discoverPlaceIdsFromGrid(CrawlGrid grid) {
      return playwrightManager.crawl(page -> {
         Set<String> collectedIds = new LinkedHashSet<>();

         String mapUrl = String.format(
                 "https://map.naver.com/p?c=%f,%f,%d,0,0,0,dh",
                 grid.centerLng(),
                 grid.centerLat(),
                 DEFAULT_ZOOM
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
            page.waitForTimeout(2_000);

            moveMapToGridCenter(page, grid);
            page.waitForTimeout(1_000);

            clickFoodCategoryButton(page);
            page.waitForTimeout(2_500);

            waitForSearchIframe(page);
            collectPlaceIdsByClickingList(page, collectedIds);

            log.info(
                    "그리드 목록 placeId 수집 완료. row={}, col={}, collectedCount={}, currentUrl={}",
                    grid.row(),
                    grid.col(),
                    collectedIds.size(),
                    safePageUrl(page)
            );
         } catch (Exception e) {
            log.warn(
                    "그리드 목록 placeId 수집 실패. grid({},{}), reason={}",
                    grid.row(),
                    grid.col(),
                    e.getMessage(),
                    e
            );
         }

         return collectedIds;
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
                     log.warn("목록 아이템 이름 비어있음으로 스킵. index={}", i);
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

   private void clickPrimaryPlaceLink(Locator item) {
      Locator primaryLink = item.locator("a.YTJkH.CtW3e").first();
      if (primaryLink.count() == 0) {
         throw new IllegalStateException("primary place link not found");
      }
      primaryLink.click(new Locator.ClickOptions().setTimeout(5_000));
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

      log.warn("초기 목록 준비 타임아웃. currentCount={}", searchFrame.locator(LIST_ITEM_SELECTOR).count());
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

         if (currentId.isPresent()) {
            if (previousId.isEmpty() || !previousId.get().equals(currentId.get())) {
               return currentId;
            }
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
      for (int i = 0; i < 30; i++) {
         Frame frame = page.frame(SEARCH_IFRAME_NAME);
         if (frame != null) {
            return;
         }
         page.waitForTimeout(200);
      }

      throw new IllegalStateException("searchIframe을 찾지 못했습니다.");
   }

   private Frame getSearchFrameOrThrow(Page page) {
      Frame frame = page.frame(SEARCH_IFRAME_NAME);
      if (frame == null) {
         throw new IllegalStateException("searchIframe을 찾지 못했습니다.");
      }
      return frame;
   }

   private void clickFoodCategoryButton(Page page) {
      try {
         Locator foodButton = page.getByRole(
                 AriaRole.BUTTON,
                 new Page.GetByRoleOptions().setName("음식점")
         );
         if (foodButton.count() > 0) {
            foodButton.first().click(new Locator.ClickOptions().setTimeout(3_000));
            return;
         }
      } catch (Exception ignored) {
      }

      try {
         Locator foodButton = page.locator("text=음식점");
         if (foodButton.count() > 0) {
            foodButton.first().click(new Locator.ClickOptions().setTimeout(3_000));
            return;
         }
      } catch (Exception ignored) {
      }

      try {
         Boolean clicked = (Boolean) page.evaluate("""
                () => {
                    const elements = Array.from(document.querySelectorAll('button, a, span, div'));
                    const target = elements.find(el => (el.innerText || '').trim() === '음식점');
                    if (!target) return false;
                    target.click();
                    return true;
                }
            """);
         if (Boolean.TRUE.equals(clicked)) {
            return;
         }
      } catch (Exception ignored) {
      }

      throw new IllegalStateException("음식점 버튼을 찾지 못했습니다.");
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

   private boolean isBlank(String value) {
      return value == null || value.isBlank();
   }

   private String safePageUrl(Page page) {
      try {
         return page.url();
      } catch (Exception e) {
         return null;
      }
   }

   private Optional<ZonePolygon> findZonePolygon(CrawlScopeType crawlScope) {
      return MapConstantsV2.ZONES.stream()
              .filter(zone -> CrawlScopeType.fromZoneType(zone.zoneType()) == crawlScope)
              .findFirst();
   }

   public record ZoneCrawlProgress(
           String phase,
           int totalGridCount,
           int processedGridCount,
           int discoveredPlaceCount,
           int totalPlaceCount,
           int attemptedPlaceCount,
           int crawledSuccessCount,
           String currentGrid,
           String currentPlaceId
   ) {
   }

   private record GridDiscoveryProgress(
           int processedGridCount,
           int discoveredPlaceCount,
           String currentGrid
   ) {
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
}
