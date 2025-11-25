package com.kustaurant.kustaurant.rating.service;

import com.kustaurant.kustaurant.rating.domain.model.AiEvaluation;
import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.service.ScoreCalculationService;
import com.kustaurant.kustaurant.rating.domain.vo.EvaluationWithContext;
import com.kustaurant.kustaurant.rating.domain.service.TierCalculationService;
import com.kustaurant.kustaurant.rating.domain.vo.GlobalStats;
import com.kustaurant.kustaurant.rating.service.port.AiSummaryRepository;
import com.kustaurant.kustaurant.rating.service.port.RatingEvaluationRepository;
import com.kustaurant.kustaurant.rating.service.port.RatingRepository;
import com.kustaurant.kustaurant.rating.service.port.RatingRestaurantRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingOrchestrationService {

    private final int CHUNK_SIZE = 100;

    private final RatingRestaurantRepository ratingRestaurantRepository;
    private final RatingEvaluationRepository ratingEvaluationRepository;

    private final ScoreCalculationService scoreCalculationService;
    private final TierCalculationService tierCalculationService;
    private final RatingRepository ratingRepository;
    private final AiSummaryRepository aiSummaryRepository;

    @Transactional
    public void calculateAllRatings() {
        List<Long> ids = ratingRestaurantRepository.getRestaurantIds();

        List<Rating> scores = calculateScores(ids);
        List<Rating> ratings = calculateTier(scores);

        ratingRepository.saveAll(ratings);
    }

    private List<Rating> calculateScores(List<Long> ids) {
        Map<Long, List<EvaluationWithContext>> selfEvalMap = ratingEvaluationRepository.getEvaluationsByRestaurantIds(ids);
        Map<Long, AiEvaluation> aiEvalMap = aiSummaryRepository.getAiEvaluations();
        GlobalStats globalStats = aiSummaryRepository.getGlobalStats();

        List<Rating> scores = scoreCalculationService.calculateScores(ids, selfEvalMap, aiEvalMap, globalStats);
        log.info("점수 계산 완료");
        return scores;
    }

    private List<Rating> calculateTier(List<Rating> scores) {
        List<Rating> temps = new ArrayList<>();
        List<Rating> notTemps = new ArrayList<>();
        for (Rating rs : scores) {
            if (rs.isTemp()) temps.add(rs);
            else notTemps.add(rs);
        }

        List<Rating> ratings = tierCalculationService.calculate(temps);
        ratings.addAll(tierCalculationService.calculate(notTemps));
        log.info("티어 계산 완료");
        return ratings;
    }
}
