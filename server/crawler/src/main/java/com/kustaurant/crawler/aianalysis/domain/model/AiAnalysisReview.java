package com.kustaurant.crawler.aianalysis.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiAnalysisReview {

    private static final int MIN_REVIEW_LENGTH = 20;

    private Long id;
    private Long jobId;
    private Long restaurantId;
    private Sentiment sentiment;
    private double score;
    private String analyzedText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AiAnalysisReview of(
            Long jobId,
            Long restaurantId,
            Sentiment sentiment,
            double score,
            String review,
            LocalDateTime now
    ) {
        return AiAnalysisReview.builder()
                .jobId(jobId)
                .restaurantId(restaurantId)
                .sentiment(sentiment)
                .score(score)
                .analyzedText(review)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static boolean isValid(String body) {
        return refineBody(body).length() > MIN_REVIEW_LENGTH;
    }

    private static String refineBody(String body) {
        return body.replaceAll("\\s+", " ").trim();
    }
}
