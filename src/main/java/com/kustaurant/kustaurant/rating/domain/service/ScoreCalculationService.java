package com.kustaurant.kustaurant.rating.domain.service;

import com.kustaurant.kustaurant.rating.domain.model.AdjustedEvaluation;
import com.kustaurant.kustaurant.rating.domain.model.EvaluationWithContext;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy;
import com.kustaurant.kustaurant.rating.domain.model.RatingScore;
import com.kustaurant.kustaurant.rating.domain.model.RestaurantStats;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreCalculationService {

    private final int MIN_EVALUATION_COUNT = 3;

    private final RatingPolicy policy;

    public RatingScore calculate(
            RestaurantStats stats,
            List<EvaluationWithContext> evaluations,
            double globalAvg,
            LocalDateTime date
    ) {
        // 평가 개수 미달인 경우 0점 처리
        if (stats.evaluationCount() < MIN_EVALUATION_COUNT) {
            return new RatingScore(stats.restaurantId(), 0);
        }

        double numerator = policy.priorWeight() * globalAvg;
        double weightSum = policy.priorWeight();

        // 각 평가를 조정해서 누적
        for (EvaluationWithContext evaluation : evaluations) {
            AdjustedEvaluation adj = evaluation.getAdjustedScore(policy.evaluation(), globalAvg, date);
            numerator += adj.adjustedScore() * adj.weight();
            weightSum += adj.weight();
        }

        // 누적 평가 점수를 가중 평균
        double coreScore = numerator / weightSum;

        // 식당의 인기도를 구함
        double popularity = stats.adjustPopularity(policy.restaurant(), coreScore);

        // 가중 평균한 값에 인기도를 곱해서 최종 점수를 구함
        double finalScore = coreScore * popularity;

        return new RatingScore(stats.restaurantId(), finalScore);
    }

    public List<RatingScore> calculateScores(
            List<RestaurantStats> statsList,
            Map<Integer, List<EvaluationWithContext>> evalMap,
            double globalAvg,
            LocalDateTime date
    ) {
        List<RatingScore> scores = new ArrayList<>(statsList.size());
        for (RestaurantStats stats : statsList) {
            RatingScore score = calculate(stats, evalMap.get(stats.restaurantId()), globalAvg, date);
            scores.add(score);
        }
        return scores;
    }
}
