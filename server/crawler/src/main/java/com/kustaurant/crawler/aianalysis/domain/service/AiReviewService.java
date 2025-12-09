package com.kustaurant.crawler.aianalysis.domain.service;

import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisReview;
import com.kustaurant.crawler.aianalysis.adapter.out.ai.ReviewAnalysis;
import com.kustaurant.crawler.aianalysis.adapter.out.ai.AiProcessor;
import com.kustaurant.crawler.aianalysis.domain.model.Sentiment;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiReviewService {

    private final AiProcessor aiProcessor;
    private final Clock clock;

    public Optional<AiAnalysisReview> analyzeReview(long jobId, long restaurantId, String review) {
        List<Optional<ReviewAnalysis>> results = new ArrayList<>();

        // 각 리뷰 별 3회 질의
        results.add(aiProcessor.analyzeReview(review));
        results.add(aiProcessor.analyzeReview(review));
        results.add(aiProcessor.analyzeReview(review));

        // 유효한 값만 모으기
        List<ReviewAnalysis> valid = results.stream()
                .filter(Objects::nonNull)
                .flatMap(Optional::stream)
                .toList();
        if (valid.isEmpty()) {
            return Optional.empty();
        }
        // 평균 점수
        double avgScore = valid.stream()
                .mapToDouble(ReviewAnalysis::score)
                .average()
                .orElse(0.0);
        // sentiment: value 평균 -> 반올림 -> 매핑
        int avgValue = (int) Math.round(
                valid.stream()
                        .mapToInt(r -> r.sentiment().getValue())
                        .average()
                        .orElse(0.0)
        );
        Sentiment sentiment = Arrays.stream(Sentiment.values())
                .filter(s -> s.getValue() == avgValue)
                .findFirst()
                .orElse(Sentiment.NEUTRAL);

        return Optional.of(
                AiAnalysisReview.of(
                        jobId,
                        restaurantId,
                        sentiment,
                        avgScore,
                        review,
                        LocalDateTime.now(clock)
                ));
    }
}
