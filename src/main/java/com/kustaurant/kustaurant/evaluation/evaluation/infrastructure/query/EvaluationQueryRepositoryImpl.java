package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.query;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EvaluationQueryRepositoryImpl implements EvaluationQueryRepository {

    private final EvaluationQueryJpaRepository jpaRepository;

    @Override
    public boolean existsByUserAndRestaurant(Long userId, Integer restaurantId) {
        if (userId == null || restaurantId == null) {
            return false;
        }
        return jpaRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Override
    public boolean existsByRestaurantAndEvaluation(Integer restaurantId, Integer evaluationId) {
        if (restaurantId == null || evaluationId == null) {
            return false;
        }
        return jpaRepository.existsByRestaurantIdAndId(restaurantId, evaluationId);
    }

    @Override
    public Optional<Evaluation> findActiveByUserAndRestaurant(Long userId, Integer restaurantId) {
        return jpaRepository.findByUserIdAndRestaurantIdAndStatus(userId, restaurantId, "ACTIVE")
                .map(EvaluationEntity::toModel);
    }
}
