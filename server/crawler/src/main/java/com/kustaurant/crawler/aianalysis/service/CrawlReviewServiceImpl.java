package com.kustaurant.crawler.aianalysis.service;

import com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto.AiAnalysisReq;
import com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto.CrawlingReviewReq;
import com.kustaurant.crawler.aianalysis.adapter.out.crawler.ReviewCrawler;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessagePublisher;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessagingProps;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiAnalysisJobRepo;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.RestaurantCrawlerRepo;
import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisJob;
import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisReview;
import com.kustaurant.crawler.aianalysis.service.port.CrawlReviewService;
import com.kustaurant.crawler.global.util.JsonUtils;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlReviewServiceImpl implements CrawlReviewService {

    private final AiAnalysisJobRepo jobRepo;
    private final RestaurantCrawlerRepo restaurantRepo;
    private final ReviewCrawler reviewCrawler;
    private final MessagePublisher<String> publisher;
    private final MessagingProps props;
    private final Clock clock;

    @Override
    @Transactional
    public void crawlReviews(CrawlingReviewReq req) {
        AiAnalysisJob job = jobRepo.findJob(req.jobId());

        try {
            List<String> reviews = getReviewsByCrawling(restaurantRepo.getRestaurantUrl(job.getRestaurantId()));

            job.startJob(reviews.size(), LocalDateTime.now(clock));

            publisher.publish(props.aiAnalysisReview(), reviews.stream()
                    .map(r -> new AiAnalysisReq(req.jobId(), job.getRestaurantId(), r))
                    .map(JsonUtils::serialize)
                    .toList());
        } catch (Exception e) {
            job.failJob(LocalDateTime.now(clock));
            log.warn("crawling and publishing failed. payload={}", req, e);
        } finally {
            jobRepo.save(job);
        }
    }

    private List<String> getReviewsByCrawling(String url) {
        return reviewCrawler
                .crawlReviews(url)
                .stream()
                .filter(AiAnalysisReview::isValid)
                .toList();
    }
}
