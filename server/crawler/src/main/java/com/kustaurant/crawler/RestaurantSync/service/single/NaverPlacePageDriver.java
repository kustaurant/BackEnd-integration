package com.kustaurant.crawler.RestaurantSync.service.single;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NaverPlacePageDriver {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(NaverPlacePageDriver.class);
   private static final String LABEL_MENU = "메뉴";

   public void openPlacePage(Page page, String placeUrl) {
      page.navigate(placeUrl, (new Page.NavigateOptions()).setTimeout((double)30000.0F));
      this.safeWaitForLoad(page, LoadState.DOMCONTENTLOADED, (double)10000.0F);
      this.waitUntilPlacePageReady(page);
      page.waitForTimeout((double)2000.0F);
   }

   public boolean clickMenuTab(Page page) {
      Optional<Frame> entryFrame = this.findEntryFrame(page);
      if (entryFrame.isPresent() && this.clickMenuTabInFrame((Frame)entryFrame.get())) {
         return true;
      } else {
         for(String selector : List.of("text=메뉴", "[role='tab']:has-text('메뉴')", "a:has-text('메뉴')", "button:has-text('메뉴')")) {
            try {
               Locator locator = page.locator(selector);
               if (locator.count() != 0) {
                  locator.first().click((new Locator.ClickOptions()).setTimeout((double)3000.0F));
                  return true;
               }
            } catch (PlaywrightException var6) {
            }
         }

         return false;
      }
   }

   public void waitForMenuIdle(Page page) {
      page.waitForTimeout((double)3000.0F);
      this.safeWaitForLoad(page, LoadState.NETWORKIDLE, (double)5000.0F);
   }

   public void navigateToDirectMenuIfNeeded(Page page, String placeId, boolean menuAlreadyCaptured) {
      if (placeId != null && !menuAlreadyCaptured) {
         String directMenuUrl = "https://pcmap.place.naver.com/restaurant/" + placeId + "/menu";

         try {
            page.navigate(directMenuUrl, (new Page.NavigateOptions()).setTimeout((double)30000.0F));
            this.safeWaitForLoad(page, LoadState.DOMCONTENTLOADED, (double)10000.0F);
            page.waitForTimeout((double)2000.0F);
         } catch (Exception e) {
            log.warn("direct menu page navigate failed. url={}", directMenuUrl, e);
         }

      }
   }

   public void navigateToDirectHomeIfNeeded(Page page, String placeId, boolean homeAlreadyCaptured) {
      if (placeId != null && !homeAlreadyCaptured) {
         String directHomeUrl = "https://pcmap.place.naver.com/restaurant/" + placeId + "/home";

         try {
            page.navigate(directHomeUrl, (new Page.NavigateOptions()).setTimeout((double)30000.0F));
            this.safeWaitForLoad(page, LoadState.DOMCONTENTLOADED, (double)10000.0F);
            page.waitForTimeout((double)2000.0F);
         } catch (Exception e) {
            log.warn("direct home page navigate failed. url={}", directHomeUrl, e);
         }

      }
   }

   private void waitUntilPlacePageReady(Page page) {
      long start = System.currentTimeMillis();

      for(long timeoutMs = 15000L; System.currentTimeMillis() - start < timeoutMs; page.waitForTimeout((double)500.0F)) {
         try {
            Optional<Frame> entryFrame = this.findEntryFrame(page);
            if (entryFrame.isPresent()) {
               Frame frame = (Frame)entryFrame.get();
               if (this.hasAnySelector(frame, "text=메뉴", "[role='tab']:has-text('메뉴')", "span.GHAhO", "div.zD5Nm", "h2")) {
                  return;
               }
            }

            if (this.hasAnySelector(page, "text=메뉴", "[role='tab']:has-text('메뉴')", "span.GHAhO", "div.zD5Nm", "h2")) {
               return;
            }
         } catch (Exception var8) {
         }
      }

   }

   private boolean clickMenuTabInFrame(Frame frame) {
      for(String selector : List.of("text=메뉴", "[role='tab']:has-text('메뉴')", "a:has-text('메뉴')", "button:has-text('메뉴')")) {
         try {
            Locator locator = frame.locator(selector);
            if (locator.count() != 0) {
               locator.first().click((new Locator.ClickOptions()).setTimeout((double)3000.0F));
               return true;
            }
         } catch (PlaywrightException var5) {
         }
      }

      return false;
   }

   private Optional findEntryFrame(Page page) {
      try {
         return page.frames().stream().filter((frame) -> "entryIframe".equals(frame.name())).findFirst();
      } catch (Exception var3) {
         return Optional.empty();
      }
   }

   private boolean hasAnySelector(Page page, String... selectors) {
      for(String selector : selectors) {
         try {
            if (page.locator(selector).count() > 0) {
               return true;
            }
         } catch (Exception var8) {
         }
      }

      return false;
   }

   private boolean hasAnySelector(Frame frame, String... selectors) {
      for(String selector : selectors) {
         try {
            if (frame.locator(selector).count() > 0) {
               return true;
            }
         } catch (Exception var8) {
         }
      }

      return false;
   }

   private void safeWaitForLoad(Page page, LoadState state, double timeoutMillis) {
      try {
         page.waitForLoadState(state, (new Page.WaitForLoadStateOptions()).setTimeout(timeoutMillis));
      } catch (PlaywrightException var6) {
      }

   }
}
