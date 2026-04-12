package com.kustaurant.crawler.RestaurantSync.service;

import com.microsoft.playwright.Response;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NaverPlaceResponseCollector {

    public void captureHtmlResponse(
            Response response,
            String placeId,
            AtomicReference<String> homeHtmlRef,
            AtomicReference<String> menuHtmlRef,
            boolean analyzeMode
    ) {
        try {
            String url = response.url();
            String contentType = response.headers().getOrDefault("content-type", "").toLowerCase(Locale.ROOT);

            if (!contentType.contains("html") || placeId == null) {
                return;
            }

            String homePath = "/restaurant/" + placeId + "/home";
            String menuPath = "/restaurant/" + placeId + "/menu";

            if (url.contains(homePath)) {
                homeHtmlRef.set(response.text());
                if (analyzeMode) {
                    log.info("captured home html response. url={}", url);
                }
            } else if (url.contains(menuPath)) {
                menuHtmlRef.set(response.text());
                if (analyzeMode) {
                    log.info("captured menu html response. url={}", url);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
