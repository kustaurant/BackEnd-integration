package com.kustaurant.crawler.infrastructure.crawler.playwright;

import com.microsoft.playwright.*;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class PlaywrightManager {
   private static final double DEFAULT_TIMEOUT_MILLIS = 10000.0;
   private static final AtomicInteger ACTIVE_BROWSERS = new AtomicInteger(0);
   private final ThreadLocal<ReusableBrowserSession> reusableSession = new ThreadLocal<>();
   private final Semaphore browserPermit = new Semaphore(1, true);

   public PlaywrightManager(MeterRegistry registry) {
      Gauge.builder("playwright_active_browsers", ACTIVE_BROWSERS, AtomicInteger::get)
              .description("현재 실행 중인 Playwright 브라우저 수").register(registry);
   }

   public <T> T crawl(Function<Page, T> function) {
      ReusableBrowserSession session = reusableSession.get();
      if (session != null) {
         try (Page page = createPage(session.context())) {
            return function.apply(page);
         }
      }

      acquireBrowserPermit();
      ACTIVE_BROWSERS.incrementAndGet();

      try (Playwright pw = createPlaywright()) {
         try (Browser browser = createBrowser(pw)) {
            try (BrowserContext context = createContext(browser)) {
               try (Page page = createPage(context)) {
                  return function.apply(page);
               }
            }
         }
      } finally {
         ACTIVE_BROWSERS.decrementAndGet();
         browserPermit.release();
      }
   }

   public <T> T withReusableBrowser(Supplier<T> function) {
      if (reusableSession.get() != null) {
         return function.get();
      }

      acquireBrowserPermit();
      ACTIVE_BROWSERS.incrementAndGet();
      try (Playwright pw = createPlaywright()) {
         try (Browser browser = createBrowser(pw)) {
            try (BrowserContext context = createContext(browser)) {
               reusableSession.set(new ReusableBrowserSession(context));
               try {
                  return function.get();
               } finally {
                  reusableSession.remove();
               }
            }
         }
      } finally {
         ACTIVE_BROWSERS.decrementAndGet();
         browserPermit.release();
      }
   }

   private void acquireBrowserPermit() {
      try {
         browserPermit.acquire();
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new IllegalStateException("Playwright 브라우저 실행 대기 중 인터럽트되었습니다.", e);
      }
   }

   private static BrowserContext createContext(Browser browser) {
      return browser.newContext((new Browser.NewContextOptions())
              .setViewportSize(3840, 2160)
              .setIsMobile(false)
              .setHasTouch(false)
              .setLocale("ko-KR")
              .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0"));
   }

   private static Page createPage(BrowserContext context) {
      Page page = context.newPage();
      page.setDefaultTimeout(DEFAULT_TIMEOUT_MILLIS);
      return page;
   }

   private record ReusableBrowserSession(BrowserContext context) {
   }

   private static Playwright createPlaywright() {
      return Playwright.create();
   }

   private static Browser createBrowser(Playwright pw) {
      return pw.chromium().launch((new BrowserType.LaunchOptions())
                      .setHeadless(true)
                      .setArgs(List.of("--disable-blink-features=AutomationControlled", "--disable-dev-shm-usage", "--no-sandbox"))
      );
   }
}
