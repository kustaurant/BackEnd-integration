package com.kustaurant.crawler.RestaurantSync.service;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NaverPlacePageDriver {

    private static final String LABEL_MENU = "메뉴";

    public void openPlacePage(Page page, String placeUrl) {
        page.navigate(placeUrl, new Page.NavigateOptions().setTimeout(30_000));
        safeWaitForLoad(page, LoadState.DOMCONTENTLOADED, 10_000);
        waitUntilPlacePageReady(page);
        page.waitForTimeout(2_000);
    }

    public boolean clickMenuTab(Page page) {
        Optional<Frame> entryFrame = findEntryFrame(page);
        if (entryFrame.isPresent() && clickMenuTabInFrame(entryFrame.get())) {
            return true;
        }

        for (String selector : List.of(
                "text=" + LABEL_MENU,
                "[role='tab']:has-text('" + LABEL_MENU + "')",
                "a:has-text('" + LABEL_MENU + "')",
                "button:has-text('" + LABEL_MENU + "')"
        )) {
            try {
                Locator locator = page.locator(selector);
                if (locator.count() == 0) {
                    continue;
                }
                locator.first().click(new Locator.ClickOptions().setTimeout(3_000));
                return true;
            } catch (PlaywrightException ignored) {
            }
        }
        return false;
    }

    public void waitForMenuIdle(Page page) {
        page.waitForTimeout(3_000);
        safeWaitForLoad(page, LoadState.NETWORKIDLE, 5_000);
    }

    public void navigateToDirectMenuIfNeeded(Page page, String placeId, boolean menuAlreadyCaptured) {
        if (placeId == null || menuAlreadyCaptured) {
            return;
        }

        String directMenuUrl = "https://pcmap.place.naver.com/restaurant/" + placeId + "/menu";
        try {
            page.navigate(directMenuUrl, new Page.NavigateOptions().setTimeout(30_000));
            safeWaitForLoad(page, LoadState.DOMCONTENTLOADED, 10_000);
            page.waitForTimeout(2_000);
        } catch (Exception e) {
            log.warn("direct menu page navigate failed. url={}", directMenuUrl, e);
        }
    }

    private void waitUntilPlacePageReady(Page page) {
        long start = System.currentTimeMillis();
        long timeoutMs = 15_000;

        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                Optional<Frame> entryFrame = findEntryFrame(page);
                if (entryFrame.isPresent()) {
                    Frame frame = entryFrame.get();
                    if (hasAnySelector(frame,
                            "text=" + LABEL_MENU,
                            "[role='tab']:has-text('" + LABEL_MENU + "')",
                            "span.GHAhO",
                            "div.zD5Nm",
                            "h2")) {
                        return;
                    }
                }

                if (hasAnySelector(page,
                        "text=" + LABEL_MENU,
                        "[role='tab']:has-text('" + LABEL_MENU + "')",
                        "span.GHAhO",
                        "div.zD5Nm",
                        "h2")) {
                    return;
                }
            } catch (Exception ignored) {
            }

            page.waitForTimeout(500);
        }
    }

    private boolean clickMenuTabInFrame(Frame frame) {
        for (String selector : List.of(
                "text=" + LABEL_MENU,
                "[role='tab']:has-text('" + LABEL_MENU + "')",
                "a:has-text('" + LABEL_MENU + "')",
                "button:has-text('" + LABEL_MENU + "')"
        )) {
            try {
                Locator locator = frame.locator(selector);
                if (locator.count() == 0) {
                    continue;
                }
                locator.first().click(new Locator.ClickOptions().setTimeout(3_000));
                return true;
            } catch (PlaywrightException ignored) {
            }
        }
        return false;
    }

    private Optional<Frame> findEntryFrame(Page page) {
        try {
            return page.frames().stream()
                    .filter(frame -> "entryIframe".equals(frame.name()))
                    .findFirst();
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private boolean hasAnySelector(Page page, String... selectors) {
        for (String selector : selectors) {
            try {
                if (page.locator(selector).count() > 0) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private boolean hasAnySelector(Frame frame, String... selectors) {
        for (String selector : selectors) {
            try {
                if (frame.locator(selector).count() > 0) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private void safeWaitForLoad(Page page, LoadState state, double timeoutMillis) {
        try {
            page.waitForLoadState(state, new Page.WaitForLoadStateOptions().setTimeout(timeoutMillis));
        } catch (PlaywrightException ignored) {
        }
    }
}
