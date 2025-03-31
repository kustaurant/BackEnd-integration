package com.kustaurant.kustaurant.common.evaluation.service.port;

import com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation.EvaluationEntity;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.Evaluation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId);

    List<EvaluationEntity> findByRestaurantIdAndStatus(Integer restaurantId, String status);


    // TODO: need to delete everything below this

    EvaluationEntity save(EvaluationEntity evaluation);
    Optional<Evaluation> findByUserAndRestaurant(UserEntity UserEntity, RestaurantEntity restaurant);
    Optional<EvaluationEntity> findByUserAndRestaurantAndStatus(UserEntity UserEntity, RestaurantEntity restaurant, String status);

    Integer countByRestaurant(RestaurantEntity restaurant);

    Integer countAllByStatus(String status);

    Optional<EvaluationEntity> findByEvaluationIdAndStatus(Integer evaluationId, String status);

    List<Evaluation> findByStatus(String status);

}
