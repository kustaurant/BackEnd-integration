package com.kustaurant.crawler.aianalysis.adapter.out.crawler.playwright;

import static com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightTools.clickBySelector;
import static com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightTools.clickTabByName;
import static com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightTools.existsTargetSelector;
import static com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightTools.getFrameLocator;
import static com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightTools.scrollerUntilTargetSelector;

import com.kustaurant.crawler.aianalysis.adapter.out.crawler.ReviewCrawler;
import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.microsoft.playwright.FrameLocator;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaywrightReviewCrawler implements ReviewCrawler {

    private static final int MAX_MORE_CLICK_COUNT = 20;

    @Override
    public List<String> crawlReviews(String url) {
        List<String> reviews = new ArrayList<>();

        return PlaywrightManager.crawl(page -> {
            // 사이트 이동
            page.navigate(url);
            // iframe#entryIframe 으로 이동
            FrameLocator entryFrame = getFrameLocator(page, "iframe#entryIframe");
            // 리뷰 버튼 클릭
            clickTabByName(entryFrame, "리뷰");
            // 리뷰 끝까지 스크롤
            String moreBtnSelector = "a.fvwqf";
            int count = 0;
            while (existsTargetSelector(entryFrame, moreBtnSelector) && count < MAX_MORE_CLICK_COUNT) {
                scrollerUntilTargetSelector(entryFrame, moreBtnSelector);
                clickBySelector(entryFrame, moreBtnSelector);
                count++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // 리뷰 읽어오기
            return entryFrame.locator("ul#_review_list > li > div.pui__vn15t2 > a")
                            .allInnerTexts();
        });
    }
}
