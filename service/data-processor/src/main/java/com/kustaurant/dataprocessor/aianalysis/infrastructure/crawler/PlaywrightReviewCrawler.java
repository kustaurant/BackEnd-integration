package com.kustaurant.dataprocessor.aianalysis.infrastructure.crawler;

import static com.kustaurant.dataprocessor.infrastructure.crawler.playwright.PlaywrightManager.closeResourcesByThread;
import static com.kustaurant.dataprocessor.infrastructure.crawler.playwright.PlaywrightManager.getPage;
import static com.kustaurant.dataprocessor.infrastructure.crawler.playwright.PlaywrightTools.clickBySelector;
import static com.kustaurant.dataprocessor.infrastructure.crawler.playwright.PlaywrightTools.clickTabByName;
import static com.kustaurant.dataprocessor.infrastructure.crawler.playwright.PlaywrightTools.existsTargetSelector;
import static com.kustaurant.dataprocessor.infrastructure.crawler.playwright.PlaywrightTools.getFrameLocator;
import static com.kustaurant.dataprocessor.infrastructure.crawler.playwright.PlaywrightTools.scrollerUntilTargetSelector;

import com.kustaurant.dataprocessor.aianalysis.domain.service.port.ReviewCrawler;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Page;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaywrightReviewCrawler implements ReviewCrawler {

    @Override
    public List<String> crawlReviews(String url) {
        List<String> reviews;

        try {
            Page page = getPage();
            // 사이트 이동
            page.navigate(url);
            // iframe#entryIframe 으로 이동
            FrameLocator entryFrame = getFrameLocator(page, "iframe#entryIframe");
            // 리뷰 버튼 클릭
            clickTabByName(entryFrame, "리뷰");
            // 리뷰 끝까지 스크롤
            String moreBtnSelector = "a.fvwqf";
            while (existsTargetSelector(entryFrame, moreBtnSelector)) {
                scrollerUntilTargetSelector(entryFrame, moreBtnSelector);
                clickBySelector(entryFrame, moreBtnSelector);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // 리뷰 읽어오기
            reviews = new ArrayList<>(
                    entryFrame.locator("ul#_review_list > li > div.pui__vn15t2 > a")
                            .allInnerTexts());
        } finally {
            closeResourcesByThread();
        }

        return reviews;
    }
}
