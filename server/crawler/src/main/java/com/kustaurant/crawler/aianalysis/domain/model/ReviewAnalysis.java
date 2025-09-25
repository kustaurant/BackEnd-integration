package com.kustaurant.crawler.aianalysis.domain.model;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ReviewAnalysis(
        Review review,
        int score,
        Sentiment sentiment,
        List<String> situations
) {

    public static Optional<ReviewAnalysis> error(String body, String message) {
        log.error(body, message);
        return Optional.empty();
    }
}
