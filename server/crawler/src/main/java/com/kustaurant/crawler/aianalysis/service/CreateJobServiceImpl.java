package com.kustaurant.crawler.aianalysis.service;

import com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto.CrawlingReviewReq;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessagePublisher;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessagingProps;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiAnalysisJobRepo;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.RestaurantCrawlerRepo;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.RestaurantCrawlingInfo;
import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisJob;
import com.kustaurant.crawler.aianalysis.service.port.CreateJobService;
import com.kustaurant.crawler.global.util.JsonUtils;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateJobServiceImpl implements CreateJobService {

    private final RestaurantCrawlerRepo restaurantCrawlerRepo;
    private final CreateJobTxService createJobTxService;
    private final Clock clock;

    @Override
    public int createJobs() {
        List<RestaurantCrawlingInfo> infos = restaurantCrawlerRepo.getRestaurantsForCrawling();
        LocalDateTime now = LocalDateTime.now(clock);

        for (RestaurantCrawlingInfo info : infos.subList(0, 20)) {
            createJobTxService.createJob(info, now);
        }

        return infos.size();
    }

    // 트랜잭션 적용을 위해 별도 클래스 생성
    @Service
    @RequiredArgsConstructor
    public static class CreateJobTxService {

        private final AiAnalysisJobRepo aiAnalysisJobRepo;
        private final MessagePublisher<String> publisher;
        private final MessagingProps messagingProps;

        @Transactional
        public void createJob(RestaurantCrawlingInfo info, LocalDateTime now) {
            long jobId = aiAnalysisJobRepo.save(mapToJob(info, now));
            publishCrawlingMsg(new CrawlingReviewReq(jobId));
        }

        private AiAnalysisJob mapToJob(RestaurantCrawlingInfo info, LocalDateTime now) {
            return AiAnalysisJob.create(info.restaurantId(), now);
        }

        private void publishCrawlingMsg(CrawlingReviewReq req) {
            publisher.publish(messagingProps.aiAnalysisCrawling(), JsonUtils.serialize(req));
        }
    }
}
