package com.kustaurant.kustaurant.evaluation.evaluation.service.port;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import java.util.Optional;

public interface EvaluationQueryRepository {

    boolean existsByUserAndRestaurant(Long userId, Integer restaurantId);

    boolean existsByRestaurantAndEvaluation(Integer restaurantId, Integer evaluationId);

    Optional<Evaluation> findActiveByUserAndRestaurant(Long userId, Integer restaurantId);
}
