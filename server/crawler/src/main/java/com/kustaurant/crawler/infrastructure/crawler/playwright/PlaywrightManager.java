package com.kustaurant.crawler.infrastructure.crawler.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.util.List;
import java.util.function.Function;

public class PlaywrightManager {

    private static final double DEFAULT_TIMEOUT_MILLIS = 60_000;
    private static final boolean HEADLESS_MODE = true;
    
    public static <T> T crawl(Function<Page, T> function) {
        try (Playwright pw = createPlaywright()) {
            Browser browser = createBrowser(pw);
            Page page = createPage(browser);
            return function.apply(page);
        }
    }

    private static Page createPage(Browser browser) {
        BrowserContext ctx = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1600, 900)
                .setIsMobile(false)
                .setHasTouch(false)
                .setLocale("ko-KR")
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Whale/4.34.340.19 Safari/537.36"));

        Page page = ctx.newPage();
        page.setDefaultTimeout(DEFAULT_TIMEOUT_MILLIS);

        return page;
    }

    private static Playwright createPlaywright() {
        return Playwright.create();
    }

    private static Browser createBrowser(Playwright pw) {
        return pw.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(HEADLESS_MODE)
                        .setArgs(List.of(
                                "--disable-blink-features=AutomationControlled",
                                "--disable-dev-shm-usage",
                                "--no-sandbox"
                        )));
    }
}
