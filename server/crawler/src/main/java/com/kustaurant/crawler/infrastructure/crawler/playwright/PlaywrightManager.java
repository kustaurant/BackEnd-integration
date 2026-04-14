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
   private static final double DEFAULT_TIMEOUT_MILLIS = (double)10000.0F;
   private static final boolean HEADLESS_MODE = true;
   private static final int DEFAULT_VIEWPORT_WIDTH = 3840;
   private static final int DEFAULT_VIEWPORT_HEIGHT = 2160;
   private static final AtomicInteger ACTIVE_BROWSERS = new AtomicInteger(0);

   public PlaywrightManager(MeterRegistry registry) {
      Gauge.builder("playwright_active_browsers", ACTIVE_BROWSERS, AtomicInteger::get).description("현재 실행 중인 Playwright 브라우저 수").register(registry);
   }

   public Object crawl(Function function) {
      ACTIVE_BROWSERS.incrementAndGet();

      Object var5;
      try {
         Playwright pw = createPlaywright();

         try {
            Browser browser = createBrowser(pw);

            try {
               Page page = createPage(browser);

               try {
                  var5 = function.apply(page);
               } catch (Throwable var18) {
                  if (page != null) {
                     try {
                        page.close();
                     } catch (Throwable var17) {
                        var18.addSuppressed(var17);
                     }
                  }

                  throw var18;
               }

               if (page != null) {
                  page.close();
               }
            } catch (Throwable var19) {
               if (browser != null) {
                  try {
                     browser.close();
                  } catch (Throwable var16) {
                     var19.addSuppressed(var16);
                  }
               }

               throw var19;
            }

            if (browser != null) {
               browser.close();
            }
         } catch (Throwable var20) {
            if (pw != null) {
               try {
                  pw.close();
               } catch (Throwable var15) {
                  var20.addSuppressed(var15);
               }
            }

            throw var20;
         }

         if (pw != null) {
            pw.close();
         }
      } finally {
         ACTIVE_BROWSERS.decrementAndGet();
      }

      return var5;
   }

   private static Page createPage(Browser browser) {
      BrowserContext ctx = browser.newContext((new Browser.NewContextOptions()).setViewportSize(3840, 2160).setIsMobile(false).setHasTouch(false).setLocale("ko-KR").setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0"));
      Page page = ctx.newPage();
      page.setDefaultTimeout((double)10000.0F);
      return page;
   }

   private static Playwright createPlaywright() {
      return Playwright.create();
   }

   private static Browser createBrowser(Playwright pw) {
      return pw.chromium().launch((new BrowserType.LaunchOptions()).setHeadless(true).setArgs(List.of("--disable-blink-features=AutomationControlled", "--disable-dev-shm-usage", "--no-sandbox")));
   }
}
