package com.kustaurant.crawler.IGpartnership;

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
public class Crawler {

    private final IGSessionManager sessionManager;
    private final IGFeedCollector feedCollector;
    private final RawPostDeduplicator deduplicator;
    private final CaptionParserResolver resolver;
    private final IGPostFactory postFactory;

    public List<IGPost> crawl(CrawlRequest req) {
        log.info("===== Instagram Crawler START(JSON) =====");
        log.info("accountName = {}", req.accountName());

        List<IGPost> results = new ArrayList<>();
        PartnershipCaptionParser parser = resolver.resolve(req.target());

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
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

                String caption = rawPost.caption() != null ? rawPost.caption() : "";
                log.debug("[{}/{}] code={}", idx, uniquePosts.size(), rawPost.code());
                log.trace("[{}] caption text = {}", idx, caption);

                ParsedCaption parsed = parser.parse(caption);

                if (!parsed.hasRequiredFields()) {
                    log.info("[{}] 필수 필드 부족 -> skip. partner='{}', benefit='{}', location='{}', phoneNumber='{}'",
                            idx, parsed.partner(), parsed.benefit(), parsed.location(), parsed.contact());
                    continue;
                }

                postFactory.create(rawPost, parsed)
                        .ifPresent(results::add);

                log.info("[{}] ✅ alliance post added. current results={}", idx, results.size());
            }

            browser.close();
        } catch (Exception e) {
            log.error("crawlAndSave JSON version error", e);
        }

        log.info("===== Instagram Crawler END(JSON) =====");
        log.info("Total alliance posts = {}", results.size());

        return results;
    }


}
