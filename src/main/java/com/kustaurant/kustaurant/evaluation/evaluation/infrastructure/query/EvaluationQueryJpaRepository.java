package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.query;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EvaluationQueryJpaRepository extends Repository<EvaluationEntity, Long> {

    boolean existsByUserIdAndRestaurantId(Long userId, Integer restaurantId);

    boolean existsByRestaurantIdAndId(Integer restaurantId, Integer evaluationId);

    Optional<EvaluationEntity> findByUserIdAndRestaurantIdAndStatus(Long userId, Integer restaurantId, String status);
}
