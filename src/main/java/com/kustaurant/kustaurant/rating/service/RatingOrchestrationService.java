package com.kustaurant.kustaurant.rating.service;

import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.model.RatingScore;
import com.kustaurant.kustaurant.rating.domain.service.ScoreCalculationService;
import com.kustaurant.kustaurant.rating.domain.model.EvaluationWithContext;
import com.kustaurant.kustaurant.rating.domain.model.RestaurantStats;
import com.kustaurant.kustaurant.rating.domain.service.TierCalculationService;
import com.kustaurant.kustaurant.rating.service.port.RatingEvaluationRepository;
import com.kustaurant.kustaurant.rating.service.port.RatingRepository;
import com.kustaurant.kustaurant.rating.service.port.RatingRestaurantRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingOrchestrationService {

    private final int CHUNK_SIZE = 50;

    private final RatingRestaurantRepository ratingRestaurantRepository;
    private final RatingEvaluationRepository ratingEvaluationRepository;

    private final ScoreCalculationService scoreCalculationService;
    private final TierCalculationService tierCalculationService;
    private final RatingRepository ratingRepository;

    @Transactional
    public void calculateAllRatings() {
        List<Integer> ids = ratingRestaurantRepository.getRestaurantIds();
        // Chunk 단위로 계산
        List<RatingScore> scores = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i += CHUNK_SIZE) {
            List<Integer> chunkIds = ids.subList(i, Math.min(i + CHUNK_SIZE, ids.size()));
            List<RatingScore> chunkScores = calculateScores(chunkIds);
            scores.addAll(chunkScores);
        }
        // 티어 계산
        List<Rating> ratings = tierCalculationService.calculate(scores);
        // 저장
        ratingRepository.saveAll(ratings);
    }

    private List<RatingScore> calculateScores(List<Integer> ids) {
        Map<Integer, RestaurantStats> statsMap = ratingRestaurantRepository.getRestaurantStatsByIds(
                ids);
        Map<Integer, List<EvaluationWithContext>> evalMap = ratingEvaluationRepository.getEvaluationsByRestaurantIds(
                ids);
        double globalAvg = ratingEvaluationRepository.getGlobalAvg();
        return scoreCalculationService.calculateScores(
                ids, statsMap, evalMap, globalAvg, LocalDate.now(ZoneId.of("Asia/Seoul")));
    }
}
