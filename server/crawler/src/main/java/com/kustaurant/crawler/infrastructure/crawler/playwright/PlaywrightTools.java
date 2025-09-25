package com.kustaurant.crawler.infrastructure.crawler.playwright;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Locator.WaitForOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

public class PlaywrightTools {

    public static FrameLocator getFrameLocator(Page page, String selector) {
        page.waitForSelector(selector,
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.ATTACHED)
        );
        return page.frameLocator(selector);
    }

    public static void waitBySelector(Page page, String selector) {
        page.locator(selector)
                .waitFor(new WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    public static void waitBySelector(FrameLocator frameLocator, String selector) {
        frameLocator.locator(selector)
                .waitFor(new WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    public static void clickByName(Page page, String name) {
        Locator locator = page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName(name));
        click(locator);
    }

    public static void clickByName(FrameLocator frameLocator, String name) {
        Locator locator = frameLocator.getByRole(AriaRole.BUTTON, new FrameLocator.GetByRoleOptions().setName(name));
        click(locator);
    }

    public static void clickTabByName(FrameLocator frameLocator, String name) {
        Locator locator = frameLocator.getByRole(AriaRole.TAB, new FrameLocator.GetByRoleOptions().setName(name));
        click(locator);
    }

    public static void clickBySelector(Page page, String selector) {
        Locator locator = page.locator(selector);
        click(locator);
    }

    public static void clickBySelector(FrameLocator frameLocator, String selector) {
        Locator locator = frameLocator.locator(selector);
        click(locator);
    }

    private static void click(Locator locator) {
        locator.waitFor(new WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        locator.click();
    }

    public static void scrollerUntilTargetSelector(FrameLocator frameLocator, String selector) {
        frameLocator.locator(selector).scrollIntoViewIfNeeded();
    }

    public static boolean existsTargetSelector(Page page, String selector) {
        return page.locator(selector).count() > 0;
    }

    public static boolean existsTargetSelector(FrameLocator frameLocator, String selector) {
        try {
            frameLocator.locator(selector).waitFor(new WaitForOptions().setTimeout(5000));
            return true;
        } catch (PlaywrightException e) {
            return false;
        }
    }

    public static boolean isClickable(Locator anchor) {
        if (!anchor.isVisible()) return false;
        try {
            // aria-disabled 우선 검사
            String ariaDisabled = anchor.getAttribute("aria-disabled");
            if (ariaDisabled != null) {
                return !"true".equalsIgnoreCase(ariaDisabled);
            }
            // disabled 속성 검사
            String disabled = anchor.getAttribute("disabled");
            if (disabled != null) {
                // 존재만 해도 보통 disabled 취급
                return false;
            }
        } catch (PlaywrightException ignore) {}
        // 속성이 없으면 보통 클릭 가능
        return true;
    }
}
