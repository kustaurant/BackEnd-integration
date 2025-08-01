package com.kustaurant.kustaurant.rating.domain.service;

import com.kustaurant.kustaurant.rating.domain.model.AdjustedEvaluation;
import com.kustaurant.kustaurant.rating.domain.model.EvaluationWithContext;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy;
import com.kustaurant.kustaurant.rating.domain.model.RatingScore;
import com.kustaurant.kustaurant.rating.domain.model.RestaurantStats;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreCalculationService {

    private final RatingPolicy policy;

    public RatingScore calculate(
            RestaurantStats stats,
            List<EvaluationWithContext> evaluations,
            double globalAvg,
            LocalDate date
    ) {
        double numerator = policy.priorWeight() * globalAvg;
        double weightSum = policy.priorWeight();

        for (EvaluationWithContext evaluation : evaluations) {
            AdjustedEvaluation adj = evaluation.getAdjustedScore(policy.evaluation(), globalAvg, date);
            numerator += adj.adjustedScore() * adj.weight();
            weightSum += adj.weight();
        }

        double coreScore = numerator / weightSum;

        double popularity = stats.adjustPopularity(policy.restaurant(), coreScore);

        double finalScore = coreScore * popularity;

        return new RatingScore(stats.restaurantId(), finalScore);
    }

    public List<RatingScore> calculateScores(
            List<Integer> ids,
            Map<Integer, RestaurantStats> statsMap,
            Map<Integer, List<EvaluationWithContext>> evalMap,
            double globalAvg,
            LocalDate date
    ) {
        List<RatingScore> scores = new ArrayList<>(ids.size());
        for (Integer id : ids) {
            RatingScore score = calculate(statsMap.get(id), evalMap.get(id), globalAvg, date);
            scores.add(score);
        }
        return scores;
    }
}
