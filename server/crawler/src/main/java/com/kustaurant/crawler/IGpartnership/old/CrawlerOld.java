package com.kustaurant.crawler.IGpartnership.old;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.crawler.IGpartnership.dto.CrawlRequest;
import com.kustaurant.crawler.IGpartnership.dto.RawPost;
import com.kustaurant.jpa.restaurant.IGPost;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public class CrawlerOld {
    private static final String INSTAGRAM_BASE_URL = "https://www.instagram.com";
    private static final Path STATE_PATH = Paths.get(System.getProperty("user.dir"), "ig_state.json");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${instagram.username}")
    String id;

    @Value("${instagram.password}")
    String pw;

    public List<IGPost> crawl(CrawlRequest req) {
        List<IGPost> results = new ArrayList<>();

        boolean firstRun = !Files.exists(STATE_PATH);
        log.info("===== Instagram Crawler START(JSON) =====");
        log.info("accountName = {}", req.accountName());

        // JSON에서 뽑은 raw 포스트들 버퍼
        List<RawPost> rawPosts = Collections.synchronizedList(new ArrayList<>());

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setSlowMo(50)
                            .setArgs(List.of(
                                    "--disable-blink-features=AutomationControlled",
                                    "--no-sandbox"
                            ))
            );

            Browser.NewContextOptions ctxOpt = new Browser.NewContextOptions()
                    .setViewportSize(1200, 720)
                    .setLocale("ko-KR")
                    .setUserAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                    "Chrome/122.0.0.0 Safari/537.36"
                    );

            if (!firstRun) {
                ctxOpt.setStorageStatePath(STATE_PATH);
                log.info("Loaded storageState from {}", STATE_PATH.toAbsolutePath());
            }

            BrowserContext context = browser.newContext(ctxOpt);

            // 최초 한 번만 수동/자동 로그인해서 state 저장
            if (firstRun) {
                log.info("First run -> doing login & save state");
                doLoginAndSaveState(context);
                log.info("State saved -> {}", STATE_PATH.toAbsolutePath());
            }

            Page page = context.newPage();
            blockImages(page);

            // **피드 JSON 응답 가로채는 리스너 등록**
            attachFeedJsonListener(page, rawPosts);

            String profileUrl = INSTAGRAM_BASE_URL + "/" + req.accountName() + "/";
            log.info("Navigate profile: {}", profileUrl);

            page.navigate(profileUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            page.waitForTimeout(3000);

            // 혹시 모를 로그인/앱 설치 팝업 닫기
            closeLoginPopup(page);

            // 프로필 피드 끝까지 스크롤해서 JSON을 최대한 많이 받게 함
            scrollToLoadAllFeed(page, rawPosts);

            log.info("Raw posts collected from JSON = {}", rawPosts.size());

            // code 기준 중복 제거 (내부 원인으로 요청이 중복되면 중복값이 발생할 수 있음)
            Map<String, RawPost> byCode = new LinkedHashMap<>();
            for (RawPost rp : rawPosts) {
                if (rp.code == null || rp.code.isBlank()) continue;
                byCode.putIfAbsent(rp.code, rp);
            }
            log.info("Unique posts by code = {}", byCode.size());

            int idx = 0;
            for (RawPost rp : byCode.values()) {
                idx++;
                String postUrl = INSTAGRAM_BASE_URL + "/p/" + rp.code + "/";
                String caption = rp.caption != null ? rp.caption : "";

                log.debug("[{}/{}] code={}, url={}", idx, byCode.size(), rp.code, postUrl);
                log.trace("[{}] caption text = {}", idx, caption);

                // 캡션 파싱
                CaptionParser.Parsed parsed =
                        CaptionParser.parse(caption);

                // 필터링 규칙
                // 1) 제휴업체(따옴표 패턴) 있어야 하고
                // 2) 혜택/위치/연락처까지 4개 다 채워진 것만 통과
                if (parsed.partner() == null || parsed.partner().isBlank()
                        || parsed.benefit() == null || parsed.benefit().isBlank()
                        || parsed.location() == null || parsed.location().isBlank()
                        || parsed.contact() == null || parsed.contact().isBlank()) {
                    log.info("[{}] 필수 필드 부족 -> skip. restaurantName='{}', benefit='{}', location='{}', phoneNumber='{}'",
                            idx, parsed.partner(), parsed.benefit(), parsed.location(), parsed.contact());
                    continue;
                }

                results.add(new IGPost(
                        postUrl,
                        parsed.partner(),
                        parsed.benefit(),
                        parsed.location(),
                        parsed.contact()
                ));

                log.info("[{}] ✅ alliance post added. current results={}", idx, results.size());
            }

            browser.close();
        } catch (Exception e) {
            log.error("crawlAndSave JSON version error", e);
        }

        log.info("===== Instagram Crawler END(JSON) =====");
        log.info("Total alliance posts = {}", results.size());

        return results;
    }

    /**
     * 페이지에서 나오는 모든 Response 중,
     * feed user_timeline JSON 응답만 골라서 파싱하는 리스너.
     */
    private void attachFeedJsonListener(Page page, List<RawPost> rawPosts) {
        page.onResponse(response -> {
            String url = response.url();
            try {
                // 인스타 프로필 피드는 /graphql/query 로 옴
                if (!url.contains("/graphql/query")) return;

                String body = response.text();
                // body 안에 우리가 찾는 키가 있는지로 필터링
                if (!body.contains("xdt_api__v1__feed__user_timeline_graphql_connection")) return;

                log.info("👍 feed JSON candidate detected. url={}", url);
                parseFeedJson(body, rawPosts);
            } catch (Exception e) {
                log.warn("Failed to parse feed JSON from url={}", url, e);
            }
        });
    }

    /**
     * xdt_api__v1__feed__user_timeline_graphql_connection JSON 파싱
     * root.data.xdt_api__v1__feed__user_timeline_graphql_connection.edges[].node.{code, caption.text}
     */
    private void parseFeedJson(String body, List<RawPost> out) throws JsonProcessingException {
        JsonNode root = MAPPER.readTree(body);
        JsonNode edges = root.path("data")
                .path("xdt_api__v1__feed__user_timeline_graphql_connection")
                .path("edges");

        if (!edges.isArray()) return;

        int added = 0;
        for (JsonNode edge : edges) {
            JsonNode node = edge.path("node");
            String code = node.path("code").asText("");
            String captionText = "";

            JsonNode captionNode = node.path("caption");
            if (!captionNode.isMissingNode()) {
                captionText = captionNode.path("text").asText("");
            }

            if (code == null || code.isBlank() || captionText == null || captionText.isBlank()) {
                continue;
            }

            out.add(new RawPost(code, captionText));
            added++;
        }

        if (added > 0) {
            log.info("Feed JSON parsed: {} posts added (total buffer size={})", added, out.size());
        }
    }

    /**
     * 프로필 피드 끝까지 스크롤해서 user_timeline JSON 요청을 최대한 발생시키는 부분
     */
    private void scrollToLoadAllFeed(Page page, List<RawPost> rawPosts) {
        int stableCount = 0;
        int lastSize = 0;

        for (int i = 0; i < 50; i++) { // 최대 50번 스크롤 시도
            int curSize = rawPosts.size();
            log.info("Scroll #{}, current rawPosts size={}", i + 1, curSize);

            // 더 이상 새로운 포스트가 안들어오면 몇 번 더 보고 중단
            if (curSize == lastSize) {
                stableCount++;
                if (stableCount >= 3) {
                    log.info("No new posts from JSON after {} tries. Stop scrolling.", stableCount);
                    break;
                }
            } else {
                stableCount = 0;
                lastSize = curSize;
            }

            // 스크롤
            page.mouse().wheel(0, 2500);
            // 네트워크/렌더링 대기
            page.waitForTimeout(2000);
        }
    }

    /**
     * 최초 1회 로그인해서 storageState 저장
     */
    private void doLoginAndSaveState(BrowserContext context) {
        log.info("step0: entered doLoginAndSaveState");
        log.info("id = {}", id);
        log.info("pw exists = {}", pw != null && !pw.isBlank());

        if (id == null || id.isBlank() || pw == null || pw.isBlank()) {
            throw new IllegalStateException("instagram.username / instagram.password 값이 비어 있음");
        }

        Page loginPage = context.newPage();

        loginPage.navigate(INSTAGRAM_BASE_URL + "/accounts/login/",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        loginPage.waitForTimeout(5000);

        log.info("url = {}", loginPage.url());
        log.info("title = {}", loginPage.title());
//
//        List<String> inputs = (List<String>) loginPage.locator("input").evaluateAll(
//                "els => els.map(e => e.outerHTML)"
//        );
//        log.info("all inputs = {}", inputs);

        Locator usernameInput = loginPage.locator(
                "input[name='email'], input[autocomplete*='username'], input[type='text']"
        ).first();

        Locator passwordInput = loginPage.locator(
                "input[name='pass'], input[autocomplete*='password'], input[type='password']"
        ).first();

        log.info("username candidate count = {}", loginPage.locator(
                "input[name='username'], input[autocomplete='username'], input[aria-label*='사용자'], input[aria-label*='username'], input[type='text']"
        ).count());

        log.info("password candidate count = {}", loginPage.locator(
                "input[name='password'], input[name='enc_password'], input[autocomplete='current-password'], input[aria-label*='비밀번호'], input[aria-label*='password'], input[type='password']"
        ).count());

        usernameInput.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        passwordInput.waitFor(new Locator.WaitForOptions().setTimeout(10000));

        usernameInput.fill(id);
        passwordInput.fill(pw);

        loginPage.keyboard().press("Enter");

        loginPage.waitForTimeout(7000);

        context.storageState(new BrowserContext.StorageStateOptions()
                .setPath(STATE_PATH));

        loginPage.close();
    }

    private void blockImages(Page page) {
        page.route("**/*", route -> {
            String url = route.request().url().toLowerCase();
            if (url.matches(".*\\.(png|jpg|jpeg|gif|webp)(\\?.*)?$")) {
                route.abort();
            } else {
                route.resume();
            }
        });
    }

    private void closeLoginPopup(Page page) {
        try {
            Locator laterButton = page.locator("button:has-text('나중에 하기')");
            if (laterButton.count() > 0) laterButton.first().click();
        } catch (Exception ignored) {}

        try {
            Locator notNowButton = page.locator("button:has-text('Not Now')");
            if (notNowButton.count() > 0) notNowButton.first().click();
        } catch (Exception ignored) {}
    }
}
