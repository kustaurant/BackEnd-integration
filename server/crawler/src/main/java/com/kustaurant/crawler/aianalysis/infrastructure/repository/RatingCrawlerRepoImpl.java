package com.kustaurant.crawler.aianalysis.infrastructure.repository;

import com.kustaurant.crawler.aianalysis.domain.model.RestaurantAnalysis;
import com.kustaurant.crawler.aianalysis.service.port.RatingCrawlerRepo;
import com.kustaurant.jpa.rating.entity.RatingEntity;
import com.kustaurant.jpa.rating.repository.RatingJpaRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RatingCrawlerRepoImpl implements RatingCrawlerRepo {

    private final RatingJpaRepository ratingJpaRepository;
    private final Clock clock;

    @Override
    public void upsertRating(long restaurantId, RestaurantAnalysis analysis) {
        update(restaurantId, analysis);
        try {
            save(restaurantId, analysis);
        } catch (DataIntegrityViolationException e) {
            update(restaurantId, analysis);
        }
    }

    private void save(long restaurantId, RestaurantAnalysis analysis) {
        RatingEntity entity = RatingEntity.of(
                restaurantId,
                analysis.getReviewCount(),
                analysis.getPositiveCount(),
                analysis.getNegativeCount(),
                analysis.getScoreSum(),
                analysis.getAvgScore(),
                LocalDateTime.now(clock)
        );
        ratingJpaRepository.save(entity);
    }

    private void update(long restaurantId, RestaurantAnalysis analysis) {
        Optional<RatingEntity> optional = ratingJpaRepository.findByIdForUpdate(restaurantId);
        if (optional.isPresent()) { // 존재할 경우 dirty check 이용
            RatingEntity ratingEntity = optional.get();
            ratingEntity.updateAiData(
                    analysis.getReviewCount(),
                    analysis.getPositiveCount(),
                    analysis.getNegativeCount(),
                    analysis.getScoreSum(),
                    analysis.getAvgScore(),
                    LocalDateTime.now(clock)
            );
        }
    }
}
