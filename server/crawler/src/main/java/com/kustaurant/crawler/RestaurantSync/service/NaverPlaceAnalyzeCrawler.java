package com.kustaurant.crawler.RestaurantSync.service;

import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.NaverPlaceMenu;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.LoadState;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverPlaceAnalyzeCrawler {  // 성공적으로 크롤하기위해 내부 구조를 파악하기 위한 용도의 crawler service layer 입니다.

    private static final String LABEL_MENU = "메뉴";
    private static final int MAX_MENU_COUNT = 50;

    private static final Pattern PLACE_ID_PATTERN = Pattern.compile("/place/(\\d+)");
    private static final Pattern LAT_PATTERN = Pattern.compile("\"(?:lat|latitude|y)\"\\s*:\\s*\"?(-?[0-9]+\\.[0-9]+)\"?");
    private static final Pattern LNG_PATTERN = Pattern.compile("\"(?:lng|longitude|x)\"\\s*:\\s*\"?(-?[0-9]+\\.[0-9]+)\"?");
    private static final Pattern PHONE_PATTERN =  Pattern.compile("((?:0507|0\\d{1,3})-\\d{3,4}-\\d{4})");
    private final PlaywrightManager playwrightManager;

    public NaverPlaceCrawlResult analyze(String placeUrl) {
        return playwrightManager.crawl(page -> {
            String placeId = extractPlaceId(placeUrl);

            AtomicReference<String> homeHtmlRef = new AtomicReference<>();
            AtomicReference<String> menuHtmlRef = new AtomicReference<>();

            page.onResponse(response -> captureHtmlResponses(response, placeId, homeHtmlRef, menuHtmlRef));

            log.info("=== NAVER PLACE ANALYZE START ===");
            log.info("targetUrl={}", placeUrl);

            // 1) 홈 진입
            page.navigate(placeUrl, new Page.NavigateOptions().setTimeout(30_000));
            safeWaitForLoad(page, LoadState.DOMCONTENTLOADED, 10_000);
            waitUntilPlacePageReady(page);
            page.waitForTimeout(2_000);

            // 2) 메뉴 탭 클릭
            boolean menuClicked = clickMenuTab(page);
            log.info("menuTabClicked={}", menuClicked);

            if (menuClicked) {
                page.waitForTimeout(3_000);
                safeWaitForLoad(page, LoadState.NETWORKIDLE, 5_000);
            }

            // 3) direct /menu도 한번 시도
            if (placeId != null && menuHtmlRef.get() == null) {
                String directMenuUrl = "https://pcmap.place.naver.com/restaurant/" + placeId + "/menu";
                try {
                    page.navigate(directMenuUrl, new Page.NavigateOptions().setTimeout(30_000));
                    safeWaitForLoad(page, LoadState.DOMCONTENTLOADED, 10_000);
                    page.waitForTimeout(2_000);
                } catch (Exception e) {
                    log.warn("direct menu page navigate failed. url={}", directMenuUrl, e);
                }
            }

            String homeHtml = homeHtmlRef.get();
            String menuHtml = menuHtmlRef.get();

            Document homeDoc = isBlank(homeHtml) ? null : Jsoup.parse(homeHtml);
            Document menuDoc = isBlank(menuHtml) ? null : Jsoup.parse(menuHtml);

            String sourceUrl = safePageUrl(page);

            String placeName = sanitizePlaceName(extractPlaceName(homeDoc));
            String category = sanitizeCategory(extractCategory(homeDoc));
            String restaurantAddress = extractRestaurantAddress(homeDoc);
            String phoneNumber = extractPhoneNumber(homeDoc);
            Double latitude = parseCoordinate(homeHtml, LAT_PATTERN);
            Double longitude = parseCoordinate(homeHtml, LNG_PATTERN);
            String imageUrl = extractMeta(homeDoc, "meta[property=og:image]", "content");

            List<NaverPlaceMenu> menus = extractMenusFromMenuHtml(menuDoc);

            // menu html에서 못 뽑으면, 메뉴 클릭 후 실제 DOM에서 fallback
            if (menus.isEmpty()) {
                menus = extractMenusFromLiveDom(page);
            }

            NaverPlaceCrawlResult result = new NaverPlaceCrawlResult(
                    placeId,
                    isBlank(sourceUrl) ? placeUrl : sourceUrl,
                    placeName,
                    category,
                    restaurantAddress,
                    phoneNumber,
                    latitude,
                    longitude,
                    imageUrl,
                    menus
            );

            log.info(
                    "hybrid analyze finished. sourcePlaceId={}, placeName={}, category={}, restaurantAddress={}, phone={}, menuCount={}",
                    result.sourcePlaceId(),
                    result.placeName(),
                    result.category(),
                    result.restaurantAddress(),
                    result.phoneNumber(),
                    result.menus() == null ? 0 : result.menus().size()
            );

            return result;
        });
    }

    private void captureHtmlResponses(
            Response response,
            String placeId,
            AtomicReference<String> homeHtmlRef,
            AtomicReference<String> menuHtmlRef
    ) {
        try {
            String url = response.url();
            String contentType = response.headers().getOrDefault("content-type", "").toLowerCase(Locale.ROOT);

            if (!contentType.contains("html")) {
                return;
            }
            if (placeId == null) {
                return;
            }

            String homePath = "/restaurant/" + placeId + "/home";
            String menuPath = "/restaurant/" + placeId + "/menu";

            if (url.contains(homePath)) {
                homeHtmlRef.set(response.text());
                log.info("captured home html response. url={}", url);
            } else if (url.contains(menuPath)) {
                menuHtmlRef.set(response.text());
                log.info("captured menu html response. url={}", url);
            }
        } catch (Exception ignored) {
        }
    }

    private void waitUntilPlacePageReady(Page page) {
        long start = System.currentTimeMillis();
        long timeoutMs = 15_000;

        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                Optional<Frame> entryFrame = findEntryFrame(page);
                if (entryFrame.isPresent()) {
                    Frame frame = entryFrame.get();
                    if (hasAnySelector(frame,
                            "text=" + LABEL_MENU,
                            "[role='tab']:has-text('" + LABEL_MENU + "')",
                            "span.GHAhO",
                            "div.zD5Nm",
                            "h2")) {
                        return;
                    }
                }

                if (hasAnySelector(page,
                        "text=" + LABEL_MENU,
                        "[role='tab']:has-text('" + LABEL_MENU + "')",
                        "span.GHAhO",
                        "div.zD5Nm",
                        "h2")) {
                    return;
                }
            } catch (Exception ignored) {
            }

            page.waitForTimeout(500);
        }
    }

    private boolean clickMenuTab(Page page) {
        Optional<Frame> entryFrame = findEntryFrame(page);
        if (entryFrame.isPresent() && clickMenuTabInFrame(entryFrame.get())) {
            return true;
        }

        for (String selector : List.of(
                "text=" + LABEL_MENU,
                "[role='tab']:has-text('" + LABEL_MENU + "')",
                "a:has-text('" + LABEL_MENU + "')",
                "button:has-text('" + LABEL_MENU + "')"
        )) {
            try {
                Locator locator = page.locator(selector);
                if (locator.count() == 0) {
                    continue;
                }
                locator.first().click(new Locator.ClickOptions().setTimeout(3_000));
                return true;
            } catch (PlaywrightException ignored) {
            }
        }
        return false;
    }

    private boolean clickMenuTabInFrame(Frame frame) {
        for (String selector : List.of(
                "text=" + LABEL_MENU,
                "[role='tab']:has-text('" + LABEL_MENU + "')",
                "a:has-text('" + LABEL_MENU + "')",
                "button:has-text('" + LABEL_MENU + "')"
        )) {
            try {
                Locator locator = frame.locator(selector);
                if (locator.count() == 0) {
                    continue;
                }
                locator.first().click(new Locator.ClickOptions().setTimeout(3_000));
                return true;
            } catch (PlaywrightException ignored) {
            }
        }
        return false;
    }

    private Optional<Frame> findEntryFrame(Page page) {
        try {
            return page.frames().stream()
                    .filter(frame -> "entryIframe".equals(frame.name()))
                    .findFirst();
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private boolean hasAnySelector(Page page, String... selectors) {
        for (String selector : selectors) {
            try {
                if (page.locator(selector).count() > 0) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private boolean hasAnySelector(Frame frame, String... selectors) {
        for (String selector : selectors) {
            try {
                if (frame.locator(selector).count() > 0) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private List<NaverPlaceMenu> extractMenusFromLiveDom(Page page) {
        Optional<Frame> entryFrame = findEntryFrame(page);
        if (entryFrame.isPresent()) {
            List<NaverPlaceMenu> fromFrame = extractMenusFromLocatorSource(entryFrame.get()::locator);
            if (!fromFrame.isEmpty()) {
                return fromFrame;
            }
        }
        return extractMenusFromLocatorSource(page::locator);
    }

    private List<NaverPlaceMenu> extractMenusFromLocatorSource(java.util.function.Function<String, Locator> locatorFactory) {
        Map<String, NaverPlaceMenu> dedupe = new LinkedHashMap<>();

        for (String selector : List.of(
                "div.place_section_content li",
                "div[class*='menu'] li",
                "ul[class*='menu'] li",
                "div[role='tabpanel'] li"
        )) {
            try {
                Locator locator = locatorFactory.apply(selector);
                int count = Math.min(locator.count(), MAX_MENU_COUNT);

                for (int i = 0; i < count; i++) {
                    Locator row = locator.nth(i);

                    String name = firstRowText(row,
                            "span.lPzHi",
                            "div.place_bluelink",
                            "strong",
                            "span[class*='name']",
                            "div[class*='title']"
                    );

                    String price = firstRowText(row,
                            "div.GXS1X",
                            "span[class*='price']",
                            "em",
                            "div[class*='price']"
                    );

                    if (isBlank(name) || isNoiseMenu(name)) {
                        continue;
                    }

                    String key = normalize(name) + "|" + normalize(price);
                    dedupe.putIfAbsent(key, new NaverPlaceMenu(
                            normalize(name),
                            normalize(price),
                            firstRowAttribute(row, "src", "img")
                    ));
                }

                if (!dedupe.isEmpty()) {
                    return new ArrayList<>(dedupe.values());
                }
            } catch (Exception ignored) {
            }
        }

        return List.of();
    }

    private List<NaverPlaceMenu> extractMenusFromMenuHtml(Document doc) {
        if (doc == null) {
            return List.of();
        }

        Map<String, NaverPlaceMenu> dedupe = new LinkedHashMap<>();

        for (Element row : doc.select("div.place_section_content li, div[class*=menu] li, ul[class*=menu] li, div[role=tabpanel] li")) {
            String name = firstNonBlank(
                    text(row, "span.lPzHi"),
                    text(row, "div.place_bluelink"),
                    text(row, "strong"),
                    text(row, "span[class*=name]"),
                    text(row, "div[class*=title]")
            );

            String price = firstNonBlank(
                    text(row, "div.GXS1X"),
                    text(row, "span[class*=price]"),
                    text(row, "em"),
                    text(row, "div[class*=price]")
            );

            if (isBlank(name) || isNoiseMenu(name)) {
                continue;
            }

            String key = normalize(name) + "|" + normalize(price);
            dedupe.putIfAbsent(key, new NaverPlaceMenu(
                    normalize(name),
                    normalize(price),
                    attr(row, "img", "src")
            ));
        }

        return new ArrayList<>(dedupe.values());
    }

    private boolean isNoiseMenu(String name) {
        String normalized = normalize(name);
        return normalized.contains("전체 알림")
                || normalized.contains("즐겨찾는 서비스")
                || normalized.contains("정보")
                || normalized.contains("리뷰")
                || normalized.contains("사진")
                || normalized.length() < 2;
    }

    private String extractPlaceName(Document doc) {
        if (doc == null) return null;

        return firstNonBlank(
                text(doc, "span.GHAhO"),
                text(doc, "div.zD5Nm em"),
                text(doc, "div.zD5Nm span"),
                text(doc, "h2"),
                extractMeta(doc, "meta[property=og:title]", "content")
        );
    }

    private String extractCategory(Document doc) {
        if (doc == null) return null;

        return firstNonBlank(
                text(doc, "span.lnJFt"),
                text(doc, "span.DJJvD"),
                text(doc, "div.PIbes")
        );
    }

    private String extractRestaurantAddress(Document doc) {
        return extractInfoValueByLabel(doc, "주소");
    }

    private String extractPhoneNumber(Document doc) {
        if (doc == null) {
            return null;
        }

        for (Element el : doc.select("div, li, span, a")) {
            String txt = normalize(el.text());
            if (isBlank(txt)) {
                continue;
            }

            Matcher matcher = PHONE_PATTERN.matcher(txt);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    /**
     * 주소 블록 전체가 아니라, "주소" 라벨이 있는 정보 row에서만 찾는다.
     */
    private String extractInfoValueByLabel(Document doc, String label) {
        if (doc == null) {
            return null;
        }

        for (Element block : doc.select("li, div")) {
            String txt = normalize(block.text());
            if (isBlank(txt) || !txt.contains(label)) {
                continue;
            }

            // 너무 긴 덩어리는 제외
            if (txt.length() > 120) {
                continue;
            }

            // 주소 후보만 남긴다
            if (txt.contains("서울") || txt.contains("경기") || txt.contains("부산")
                    || txt.contains("인천") || txt.contains("대구") || txt.contains("광주")
                    || txt.contains("대전") || txt.contains("울산") || txt.contains("세종")
                    || txt.contains("제주")) {

                String cleaned = txt.replace(label, "").trim();

                if (cleaned.contains("길") || cleaned.contains("로")) {
                    return normalize(cleaned);
                }
            }
        }

        return null;
    }

    private String extractMeta(Document doc, String css, String attr) {
        Element el = doc.selectFirst(css);
        return el == null ? null : normalize(el.attr(attr));
    }

    private String text(Document doc, String css) {
        Element el = doc.selectFirst(css);
        return el == null ? null : normalize(el.text());
    }

    private String text(Element root, String css) {
        Element el = root.selectFirst(css);
        return el == null ? null : normalize(el.text());
    }

    private String attr(Element root, String css, String attr) {
        Element el = root.selectFirst(css);
        return el == null ? null : normalize(el.attr(attr));
    }

    private String firstRowText(Locator row, String... selectors) {
        for (String selector : selectors) {
            try {
                Locator locator = row.locator(selector);
                if (locator.count() == 0) {
                    continue;
                }
                String value = locator.first().innerText();
                if (!isBlank(value)) {
                    return normalize(value);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String firstRowAttribute(Locator row, String attributeName, String selector) {
        try {
            Locator locator = row.locator(selector);
            if (locator.count() == 0) {
                return null;
            }
            return normalize(locator.first().getAttribute(attributeName));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String extractPlaceId(String sourceUrl) {
        if (isBlank(sourceUrl)) {
            return null;
        }
        Matcher matcher = PLACE_ID_PATTERN.matcher(sourceUrl);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Double parseCoordinate(String html, Pattern pattern) {
        if (isBlank(html)) {
            return null;
        }

        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            return null;
        }

        try {
            return Double.parseDouble(matcher.group(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String sanitizePlaceName(String value) {
        if (isBlank(value)) {
            return null;
        }
        return normalize(
                value.replace(" : 네이버", "")
                        .replace(" - 네이버지도", "")
                        .replace(" - 네이버 지도", "")
        );
    }

    private String sanitizeCategory(String value) {
        if (isBlank(value)) {
            return null;
        }
        String normalized = normalize(value);
        if (normalized.contains("모든 여정의 시작") || normalized.contains("네이버지도")) {
            return null;
        }
        return normalized;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return normalize(value);
            }
        }
        return null;
    }

    private void safeWaitForLoad(Page page, LoadState state, double timeoutMillis) {
        try {
            page.waitForLoadState(state, new Page.WaitForLoadStateOptions().setTimeout(timeoutMillis));
        } catch (PlaywrightException ignored) {
        }
    }

    private String safePageUrl(Page page) {
        try {
            return page.url();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\s+", " ").trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
