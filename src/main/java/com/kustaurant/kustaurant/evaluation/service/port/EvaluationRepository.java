package com.kustaurant.kustaurant.evaluation.service.port;

import com.kustaurant.kustaurant.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.evaluation.infrastructure.EvaluationEntity;
import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.user.infrastructure.UserEntity;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId);
    List<EvaluationEntity> findByRestaurantIdAndStatus(Integer restaurantId, String status);
    Integer countAllByStatus(String status);
    List<EvaluationDomain> findByUserId(Integer userId);
    List<EvaluationDomain> findSortedEvaluationByUserIdDesc(Integer userId);
    // TODO: need to delete everything below this

    EvaluationEntity save(EvaluationEntity evaluation);
    Optional<EvaluationEntity> findByUserAndRestaurant(UserEntity UserEntity, RestaurantEntity restaurant);
    Optional<EvaluationEntity> findByUserAndRestaurantAndStatus(UserEntity UserEntity, RestaurantEntity restaurant, String status);

    Integer countByRestaurant(RestaurantEntity restaurant);

    Optional<EvaluationEntity> findByEvaluationIdAndStatus(Integer evaluationId, String status);

    List<EvaluationEntity> findByStatus(String status);

}
