package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface EvaluationQueryJpaRepository extends Repository<EvaluationEntity, Long> {

    boolean existsByUserIdAndRestaurantId(Long userId, Integer restaurantId);

    boolean existsByRestaurantIdAndId(Integer restaurantId, Integer evaluationId);

    Optional<EvaluationEntity> findByUserIdAndRestaurantIdAndStatus(Long userId, Integer restaurantId, String status);
}
