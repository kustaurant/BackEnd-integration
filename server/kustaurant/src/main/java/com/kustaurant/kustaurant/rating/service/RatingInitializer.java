package com.kustaurant.kustaurant.rating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Profile({"local", "prod"})
@Component
@RequiredArgsConstructor
public class RatingInitializer {

    private final RatingOrchestrationService ratingOrchestrationService;

//    @EventListener(ApplicationReadyEvent.class)
    public void ratingInit() {
        ratingOrchestrationService.calculateAllRatings();
    }
}
