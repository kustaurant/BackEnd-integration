package com.kustaurant.kustaurant.mock;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;

import java.util.List;
import java.util.Optional;

public class FakeEvaluationQueryRepository implements EvaluationQueryRepository {

    @Override
    public Evaluation findActiveById(Long id) {
        return null;
    }

    @Override
    public boolean existsByUserAndRestaurant(Long userId, Integer restaurantId) {
        return false;
    }

    @Override
    public boolean existsByRestaurantAndEvaluation(Integer restaurantId, Long evaluationId) {
        return false;
    }

    @Override
    public Optional<Evaluation> findActiveByUserAndRestaurant(Long userId, Integer restaurantId) {
        return Optional.empty();
    }

    @Override
    public int countByStatus(String status) {
        return 0;
    }

    @Override
    public List<Evaluation> findByRestaurantIdOrderByCreatedAtDesc(Integer restaurantId) {
        return List.of();
    }

    @Override
    public List<Evaluation> findByRestaurantIdOrderByLikeCountDesc(Integer restaurantId) {
        return List.of();
    }
}
