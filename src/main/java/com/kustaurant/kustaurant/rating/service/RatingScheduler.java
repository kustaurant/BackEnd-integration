package com.kustaurant.kustaurant.rating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingScheduler {

    private final RatingOrchestrationService ratingOrchestrationService;

    // 매일 새벽 3시에 진행
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void run() {
        ratingOrchestrationService.calculateAllRatings();
    }

    // 스프링 프로세스 시작 시 진행
    @EventListener(ApplicationReadyEvent.class)
    public void ratingInit() {
        ratingOrchestrationService.calculateAllRatings();
    }
}
