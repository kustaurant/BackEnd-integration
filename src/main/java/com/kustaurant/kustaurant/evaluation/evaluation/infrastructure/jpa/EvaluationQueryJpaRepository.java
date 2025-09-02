package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface EvaluationQueryJpaRepository extends Repository<EvaluationEntity, Long> {

    Optional<EvaluationEntity> findByIdAndStatus(Long id, String status);

    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);

    boolean existsByRestaurantIdAndId(Long restaurantId, Long evaluationId);

    Optional<EvaluationEntity> findByUserIdAndRestaurantIdAndStatus(Long userId, Long restaurantId, String status);

    Integer countByStatus(String status);

    List<EvaluationEntity> findByRestaurantIdAndStatusOrderByCreatedAtDesc(Long restaurantId, String status);

    List<EvaluationEntity> findByRestaurantIdAndStatusOrderByLikeCountDesc(Long restaurantId, String status);

}
