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

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void run() {
        ratingOrchestrationService.calculateAllRatings();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ratingInit() {
        ratingOrchestrationService.calculateAllRatings();
    }
}
