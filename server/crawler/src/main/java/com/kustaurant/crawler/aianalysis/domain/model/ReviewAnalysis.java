package com.kustaurant.crawler.aianalysis.domain.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ReviewAnalysis(
        Review review,
        double score,
        Sentiment sentiment,
        List<String> situations
) {

    public static Optional<ReviewAnalysis> error(String body, Throwable ex) {
        log.warn(body, ex);
        return Optional.empty();
    }

    public static Optional<ReviewAnalysis> from(Review review, List<Optional<ReviewAnalysis>> list) {
        // 유효한 값만 모으기
        List<ReviewAnalysis> valid = list.stream()
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

        return Optional.of(new ReviewAnalysis(
                review,
                avgScore,
                sentiment,
                List.of()
        ));
    }
}
