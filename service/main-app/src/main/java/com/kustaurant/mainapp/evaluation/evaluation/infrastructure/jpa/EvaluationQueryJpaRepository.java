package com.kustaurant.mainapp.evaluation.evaluation.infrastructure.jpa;

import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EvaluationQueryJpaRepository extends Repository<EvaluationEntity, Long> {

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<EvaluationEntity> findByIdAndStatus(Long id, String status);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    boolean existsByRestaurantIdAndId(Long restaurantId, Long evaluationId);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<EvaluationEntity> findByUserIdAndRestaurantIdAndStatus(Long userId, Long restaurantId, String status);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Integer countByStatus(String status);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    List<EvaluationEntity> findByRestaurantIdAndStatusOrderByCreatedAtDesc(Long restaurantId, String status);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    List<EvaluationEntity> findByRestaurantIdAndStatusOrderByLikeCountDesc(Long restaurantId, String status);

}
