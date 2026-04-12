package com.kustaurant.crawler.RestaurantSync.service;

import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.NaverPlaceMenu;
import com.microsoft.playwright.Page;
import java.util.List;
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

    public NaverPlaceCrawlResult crawl(String placeUrl) {
        return crawlInternal(placeUrl, false);
    }

    public NaverPlaceCrawlResult analyze(String placeUrl) {
        return crawlInternal(placeUrl, true);
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
                log.info("=== NAVER PLACE ANALYZE START ===");
                log.info("targetUrl={}", placeUrl);
            }

            pageDriver.openPlacePage(page, placeUrl);
            boolean menuClicked = pageDriver.clickMenuTab(page);
            if (analyzeMode) {
                log.info("menuTabClicked={}", menuClicked);
            }

            if (menuClicked) {
                pageDriver.waitForMenuIdle(page);
            }
            pageDriver.navigateToDirectMenuIfNeeded(page, placeId, menuHtmlRef.get() != null);

            String homeHtml = homeHtmlRef.get();
            String menuHtml = menuHtmlRef.get();
            Document homeDoc = isBlank(homeHtml) ? null : Jsoup.parse(homeHtml);
            Document menuDoc = isBlank(menuHtml) ? null : Jsoup.parse(menuHtml);

            String sourceUrl = safePageUrl(page);
            NaverPlaceInfoExtractor.NaverPlaceBasicInfo basicInfo = infoExtractor.extract(homeDoc, homeHtml);
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

            if (analyzeMode) {
                log.info(
                        "hybrid analyze finished. sourcePlaceId={}, placeName={}, category={}, restaurantAddress={}, phone={}, menuCount={}",
                        result.sourcePlaceId(),
                        result.placeName(),
                        result.category(),
                        result.restaurantAddress(),
                        result.phoneNumber(),
                        result.menus() == null ? 0 : result.menus().size()
                );
            } else {
                log.info(
                        "naver place crawl finished. sourcePlaceId={}, placeName={}, category={}, restaurantAddress={}, phone={}, menuCount={}",
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
}
