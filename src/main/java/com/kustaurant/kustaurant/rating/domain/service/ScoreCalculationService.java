package com.kustaurant.kustaurant.rating.domain.service;

import com.kustaurant.kustaurant.rating.domain.model.EvaluationWithContext;
import com.kustaurant.kustaurant.rating.domain.model.RatingScore;
import com.kustaurant.kustaurant.rating.domain.model.RestaurantStats;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ScoreCalculationService {

    public RatingScore calculate(RestaurantStats restaurant, EvaluationWithContext evaluation) {
        return null;
    }

    public List<RatingScore> calculateScores(List<Integer> ids, Map<Integer, RestaurantStats> statsMap,
            Map<Integer, EvaluationWithContext> evalMap) {
        List<RatingScore> scores = new ArrayList<>(ids.size());
        for (Integer id : ids) {
            RatingScore score = calculate(statsMap.get(id), evalMap.get(id));
            scores.add(score);
        }
        return scores;
    }
}
