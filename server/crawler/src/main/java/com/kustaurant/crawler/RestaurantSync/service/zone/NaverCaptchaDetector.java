package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

@Component
public class NaverCaptchaDetector {

   private static final String CAPTCHA_ROOT_SELECTOR = "#wtm-captcha-root";
   private static final String CAPTCHA_DIALOG_SELECTOR = "[role='dialog'][aria-label='보안 인증 필요']";

   public boolean isDetected(Page page) {
      return isVisible(page, CAPTCHA_ROOT_SELECTOR) || isVisible(page, CAPTCHA_DIALOG_SELECTOR);
   }

   private boolean isVisible(Page page, String selector) {
      try {
         Locator locator = page.locator(selector);
         return locator.count() > 0 && locator.first().isVisible();
      } catch (Exception ignored) {
         return false;
      }
   }
}
