package com.kustaurant.dataprocessor.infrastructure.crawler.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.util.List;

/**
 * 브라우저를 동시에 여러 개 키면 메모리 사용량이 급증하기 때문에 싱글톤 방식으로 Manager에서 관리함.
 *
 * 그리고 스레드 당 최대 하나의 페이지를 할당하기 위해 ThreadLocal을 사용함.
 */
public class PlaywrightManager {

    private static final double DEFAULT_TIMEOUT_MILLIS = 10_000;
    private static final boolean HEADLESS_MODE = false;

    private static Playwright pw;
    private static Browser browser;

    private static final ThreadLocal<BrowserContext> threadContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> threadPage = new ThreadLocal<>();

    static {
        createPlaywright();
        createBrowser();
    }

    public static Page getPage() {
        Page page = threadPage.get();
        if (page == null) {
            BrowserContext preCtx = threadContext.get();
            if (preCtx != null) { // 이전 BrowserContext_가 있을 경우 종료
                preCtx.close();
            }
            // 새로운 BrowserContext 생성
            if (browser == null) {
                createBrowser();
            }
            BrowserContext ctx = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(1600, 900)
                    .setIsMobile(false)
                    .setHasTouch(false)
                    .setLocale("ko-KR")
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/123.0.0.0 Safari/537.36"));
            page = ctx.newPage();
            page.setDefaultTimeout(DEFAULT_TIMEOUT_MILLIS);
            threadContext.set(ctx);
            threadPage.set(page);
        }
        return page;
    }

    private static void createPlaywright() {
        pw = Playwright.create();
    }

    private static void createBrowser() {
        if (pw == null) {
            createPlaywright();
        }
        browser = pw.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(HEADLESS_MODE)
                .setArgs(List.of("--disable-blink-features=AutomationControlled")));
    }

    public static void shutdown() {
        if (browser != null) {
            browser.close();
        }
        if (pw != null) {
            pw.close();
        }
    }

    public static void closeResourcesByThread() {
        // Page_를 닫은 후에 BrowserContext_를 닫음
        Page page = threadPage.get();
        if (page != null) {
            try {
                page.close();
            } finally {
                threadPage.remove();
            }
        }
        BrowserContext ctx = threadContext.get();
        if (ctx != null) {
            try {
                ctx.close();
            } finally {
                threadContext.remove();
            }
        }
    }
}
