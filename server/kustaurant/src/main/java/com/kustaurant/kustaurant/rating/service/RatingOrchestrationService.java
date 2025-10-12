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
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Transactional
    public void calculateAllRatings() {
        List<Long> ids = ratingRestaurantRepository.getRestaurantIds();
        // Chunk 단위로 점수 계산
        List<RatingScore> scores = calculateScoreByChunk(ids);
        // 티어 계산
        List<Rating> ratings = calculateTier(scores);
        // 저장
        ratingRepository.saveAll(ratings);
    }

    private List<Rating> calculateTier(List<RatingScore> scores) {
        List<Rating> ratings = tierCalculationService.calculate(scores);
        log.info("티어 계산 완료");
        return ratings;
    }

    private List<RatingScore> calculateScoreByChunk(List<Long> ids) {
        List<RatingScore> scores = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i += CHUNK_SIZE) {
            List<Long> chunkIds = ids.subList(i, Math.min(i + CHUNK_SIZE, ids.size()));
            List<RatingScore> chunkScores = calculateScores(chunkIds);
            scores.addAll(chunkScores);
            log.info("{}~{} 점수 계산 완료", i + 1, i + chunkIds.size());
        }
        return scores;
    }

    // 점수 계산 호출
    private List<RatingScore> calculateScores(List<Long> ids) {
        List<RestaurantStats> statsList = ratingRestaurantRepository
                .getRestaurantStatsByIds(ids);
        Map<Long, List<EvaluationWithContext>> evalMap = ratingEvaluationRepository
                .getEvaluationsByRestaurantIds(ids);
        double globalAvg = ratingEvaluationRepository.getGlobalAvg();

        return scoreCalculationService.calculateScores(statsList, evalMap, globalAvg);
    }
}
