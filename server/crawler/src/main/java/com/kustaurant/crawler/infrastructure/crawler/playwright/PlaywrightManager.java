package com.kustaurant.crawler.infrastructure.crawler.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class PlaywrightManager {

    private static final double DEFAULT_TIMEOUT_MILLIS = 20_000;
    private static final boolean HEADLESS_MODE = true;

    private static final AtomicInteger ACTIVE_BROWSERS = new AtomicInteger(0);

    public PlaywrightManager(MeterRegistry registry) {
        Gauge.builder("playwright_active_browsers", ACTIVE_BROWSERS, AtomicInteger::get)
                .description("현재 실행 중인 Playwright 브라우저 수")
                .register(registry);
    }
    
    public <T> T crawl(Function<Page, T> function) {
        ACTIVE_BROWSERS.incrementAndGet();

        try (Playwright pw = createPlaywright()) {
            try (Browser browser = createBrowser(pw)) {
                try (Page page = createPage(browser)) {
                    return function.apply(page);
                }
            }
        } finally {
            ACTIVE_BROWSERS.decrementAndGet();
        }
    }

    private static Page createPage(Browser browser) {
        BrowserContext ctx = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1600, 900)
                .setIsMobile(false)
                .setHasTouch(false)
                .setLocale("ko-KR")
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0"));

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
