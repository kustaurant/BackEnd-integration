package com.kustaurant.crawler.IGpartnership.service;

import com.kustaurant.crawler.IGpartnership.dto.CrawlRequest;
import com.kustaurant.crawler.IGpartnership.dto.ParsedCaption;
import com.kustaurant.crawler.IGpartnership.dto.RawPost;
import com.kustaurant.crawler.IGpartnership.service.strategy.CaptionStrategyResolver;
import com.kustaurant.crawler.IGpartnership.service.strategy.PartnershipCaptionStrategy;
import com.kustaurant.jpa.restaurant.IGPost;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class Crawler { //

    private final IGSessionManager sessionManager;
    private final IGFeedCollector feedCollector;
    private final RawPostDeduplicator deduplicator;
    private final CaptionStrategyResolver strategyResolver;
    private final IGPostFactory igPostFactory;

    public List<IGPost> crawl(CrawlRequest req) {
        log.info("===== 인스타그램 크롤링 시작 =====");
        log.info("계정명 = {}", req.accountName());
        log.info("TARGET = {}", req.target());

        List<IGPost> results = new ArrayList<>();
        PartnershipCaptionStrategy strategy = strategyResolver.resolve(req.target());

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
                            .setSlowMo(50)
                            .setArgs(List.of(
                                    "--disable-blink-features=AutomationControlled",
                                    "--no-sandbox"
                            ))
            );

            BrowserContext context = sessionManager.createContext(browser);
            Page page = context.newPage();

            List<RawPost> collected = feedCollector.collect(page, req.accountName());
            List<RawPost> uniquePosts = deduplicator.deduplicateByCode(collected);

            log.info("Unique posts by code = {}", uniquePosts.size());

            int idx = 0;
            for (RawPost rawPost : uniquePosts) {
                idx++;

                ParsedCaption parsed = strategy.parse(rawPost.caption());

                if (!strategy.hasRequiredFields(parsed)) {
                    log.info("[{}] 필수 필드 부족 -> skip. restaurantName='{}', benefit='{}', location='{}', contact='{}'",
                            idx,
                            parsed.restaurantName(),
                            parsed.benefit(),
                            parsed.location(),
                            parsed.contact());
                    continue;
                }

                igPostFactory.create(rawPost, parsed).ifPresent(results::add);
            }

            browser.close();
        } catch (Exception e) {
            log.error("crawl error", e);
        }

        log.info("===== 인스타그램 크롤링 종료 =====");
        log.info("전체 제휴 포스트 수 : {}", results.size());

        return results;
    }
}
