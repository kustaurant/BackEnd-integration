package com.kustaurant.mainapp.evaluation.evaluation.service.port;

import com.kustaurant.mainapp.evaluation.evaluation.domain.Evaluation;

import java.util.List;
import java.util.Optional;

public interface EvaluationQueryRepository {

    Evaluation findActiveById(Long id);

    boolean existsByUserAndRestaurant(Long userId, Long restaurantId);

    boolean existsByRestaurantAndEvaluation(Long restaurantId, Long evaluationId);

    Optional<Evaluation> findActiveByUserAndRestaurant(Long userId, Long restaurantId);

    int countByStatus(String status);

    List<Evaluation> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
    List<Evaluation> findByRestaurantIdOrderByLikeCountDesc(Long restaurantId);
}
