package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import javax.swing.text.html.Option;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface EvaluationQueryJpaRepository extends Repository<EvaluationEntity, Long> {

    Optional<EvaluationEntity> findByIdAndStatus(Long id, String status);

    boolean existsByUserIdAndRestaurantId(Long userId, Integer restaurantId);

    boolean existsByRestaurantIdAndId(Integer restaurantId, Long evaluationId);

    Optional<EvaluationEntity> findByUserIdAndRestaurantIdAndStatus(Long userId, Integer restaurantId, String status);

    Integer countByStatus(String status);
    List<EvaluationEntity> findByRestaurantIdAndStatusOrderByCreatedAtDesc(Integer restaurantId, String status);
    List<EvaluationEntity> findByRestaurantIdAndStatusOrderByLikeCountDesc(Integer restaurantId, String status);

}
