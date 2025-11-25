package com.kustaurant.crawler.aianalysis.service;

import com.kustaurant.crawler.aianalysis.adapter.in.messaging.MessagingProps;
import com.kustaurant.crawler.aianalysis.adapter.in.messaging.dto.AiAnalysisRequest;
import com.kustaurant.crawler.aianalysis.service.port.RestaurantCrawlerRepo;
import com.kustaurant.crawler.aianalysis.service.port.RestaurantCrawlingInfo;
import com.kustaurant.crawler.global.util.JsonUtils;
import com.kustaurant.crawler.infrastructure.messaging.MessagePublisher;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiAnalysisScheduler {

    private final RestaurantCrawlerRepo restaurantCrawlerRepo;
    private final MessagePublisher<String> messagePublisher;
    private final MessagingProps messagingProps;

    // 매주 토요일 새벽 4시에 진행
    @Scheduled(cron = "0 0 4 * * SAT", zone = "Asia/Seoul")
    public void crawlingInit() {
        List<RestaurantCrawlingInfo> infos = restaurantCrawlerRepo.getRestaurantsForCrawling();
        for (RestaurantCrawlingInfo info : infos) {
            AiAnalysisRequest req = new AiAnalysisRequest(info.restaurantId(), info.url(), List.of());
            messagePublisher.publish(messagingProps.aiAnalysisStart(), JsonUtils.serialize(req));
        }
    }
}
