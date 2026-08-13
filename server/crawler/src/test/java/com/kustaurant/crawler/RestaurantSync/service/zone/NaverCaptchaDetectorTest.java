package com.kustaurant.crawler.RestaurantSync.service.zone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

class NaverCaptchaDetectorTest {

   private final NaverCaptchaDetector detector = new NaverCaptchaDetector();

   @Test
   void detectsVisibleNaverCaptchaRoot() {
      Page page = mock(Page.class);
      Locator root = visibleLocator(true);
      Locator dialog = visibleLocator(false);
      when(page.locator("#wtm-captcha-root")).thenReturn(root);
      when(page.locator("[role='dialog'][aria-label='보안 인증 필요']")).thenReturn(dialog);

      assertThat(detector.isDetected(page)).isTrue();
   }

   @Test
   void ignoresMissingOrHiddenCaptchaElements() {
      Page page = mock(Page.class);
      Locator root = visibleLocator(false);
      Locator dialog = visibleLocator(false);
      when(page.locator("#wtm-captcha-root")).thenReturn(root);
      when(page.locator("[role='dialog'][aria-label='보안 인증 필요']")).thenReturn(dialog);

      assertThat(detector.isDetected(page)).isFalse();
   }

   private Locator visibleLocator(boolean visible) {
      Locator locator = mock(Locator.class);
      Locator first = mock(Locator.class);
      when(locator.count()).thenReturn(visible ? 1 : 0);
      when(locator.first()).thenReturn(first);
      when(first.isVisible()).thenReturn(visible);
      return locator;
   }
}
