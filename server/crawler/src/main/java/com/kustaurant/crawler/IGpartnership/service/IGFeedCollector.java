package com.kustaurant.crawler.IGpartnership.service;

import com.kustaurant.crawler.IGpartnership.dto.RawPost;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class IGFeedCollector {

    private static final String INSTAGRAM_BASE_URL = "https://www.instagram.com";

    private final IGFeedJsonParser feedJsonParser;

    public IGFeedCollector(IGFeedJsonParser feedJsonParser) {
        this.feedJsonParser = feedJsonParser;
    }

    public List<RawPost> collect(Page page, String accountName) {
        List<RawPost> rawPosts = Collections.synchronizedList(new ArrayList<>());

        blockImages(page);
        attachFeedJsonListener(page, rawPosts);

        String profileUrl = INSTAGRAM_BASE_URL + "/" + accountName + "/";
        log.info("Navigate profile: {}", profileUrl);

        page.navigate(profileUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        page.waitForTimeout(3000);

        closeLoginPopup(page);
        scrollToLoadAllFeed(page, rawPosts);

        log.info("Raw posts collected from JSON = {}", rawPosts.size());
        return rawPosts;
    }

    private void attachFeedJsonListener(Page page, List<RawPost> rawPosts) {
        page.onResponse(response -> {
            String url = response.url();

            try {
                if (!url.contains("/graphql/query")) {
                    return;
                }

                String body = response.text();
                if (!body.contains("xdt_api__v1__feed__user_timeline_graphql_connection")) {
                    return;
                }

                log.info("👍 feed JSON candidate detected. url={}", url);

                List<RawPost> parsedPosts = feedJsonParser.parse(body);
                rawPosts.addAll(parsedPosts);

                if (!parsedPosts.isEmpty()) {
                    log.info("Feed JSON parsed: {} posts added (total buffer size={})",
                            parsedPosts.size(), rawPosts.size());
                }
            } catch (Exception e) {
                log.warn("Failed to parse feed JSON from url={}", url, e);
            }
        });
    }

    private void scrollToLoadAllFeed(Page page, List<RawPost> rawPosts) {
        int stableCount = 0;
        int lastSize = 0;

        for (int i = 0; i < 50; i++) {
            int curSize = rawPosts.size();
            log.info("Scroll #{}, current rawPosts size={}", i + 1, curSize);

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

            page.mouse().wheel(0, 2500);
            page.waitForTimeout(2000);
        }
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
            if (laterButton.count() > 0) {
                laterButton.first().click();
            }
        } catch (Exception ignored) {
        }

        try {
            Locator notNowButton = page.locator("button:has-text('Not Now')");
            if (notNowButton.count() > 0) {
                notNowButton.first().click();
            }
        } catch (Exception ignored) {
        }
    }
}