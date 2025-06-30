package com.kustaurant.kustaurant.evaluation.evaluation.service.port;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.evaluation.EvaluationEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    boolean existsByUserAndRestaurant(Long userId, Integer restaurantId);
    List<EvaluationEntity> findByRestaurantIdAndStatus(Integer restaurantId, String status);
    Integer countAllByStatus(String status);
    List<Evaluation> findByUserId(Long userId);
    List<Evaluation> findSortedEvaluationByUserIdDesc(Long userId);
    // TODO: need to delete everything below this

    EvaluationEntity save(EvaluationEntity evaluation);
    Optional<EvaluationEntity> findByUserAndRestaurant(Long userId, RestaurantEntity restaurant);
    Optional<EvaluationEntity> findByUserAndRestaurantAndStatus(Long userId, RestaurantEntity restaurant, String status);

    Integer countByRestaurant(RestaurantEntity restaurant);

    Optional<EvaluationEntity> findByEvaluationIdAndStatus(Integer evaluationId, String status);

    List<EvaluationEntity> findByStatus(String status);

}
