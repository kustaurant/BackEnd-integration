package com.kustaurant.crawler.aianalysis.service;

import com.kustaurant.crawler.aianalysis.messaging.AiAnalysisSubscriber;
import com.kustaurant.crawler.aianalysis.messaging.MessagingProps;
import com.kustaurant.crawler.aianalysis.messaging.dto.AiAnalysisRequest;
import com.kustaurant.crawler.aianalysis.service.port.RestaurantCrawlerRepo;
import com.kustaurant.crawler.aianalysis.service.port.RestaurantCrawlingInfo;
import com.kustaurant.crawler.global.util.JsonUtils;
import com.kustaurant.crawler.infrastructure.messaging.MessagePublisher;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiAnalysisInitializer {

    private final RestaurantCrawlerRepo restaurantCrawlerRepo;
    private final MessagePublisher<String> messagePublisher;
    private final MessagingProps messagingProps;

//    @EventListener(ApplicationReadyEvent.class)
    public void crawlingInit() throws InterruptedException {
        // 기존 큐 검사 작업 먼저 진행하기 위해.
        Thread.sleep(1000);
        AiAnalysisSubscriber.readLock().lock();

        try {
            List<RestaurantCrawlingInfo> infos = restaurantCrawlerRepo.getRestaurantsForCrawling();
//            infos = infos.subList(0, Math.min(100, infos.size()));
            for (RestaurantCrawlingInfo info : infos) {
                AiAnalysisRequest req = new AiAnalysisRequest(info.restaurantId(), info.url(), List.of());
                messagePublisher.publish(messagingProps.aiAnalysisStart(),
                        JsonUtils.serialize(req));
            }
        } finally {
            AiAnalysisSubscriber.readLock().unlock();
        }
    }
}
