package com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationJpaRepository extends JpaRepository<EvaluationEntity, Integer>{
    boolean existsByUser_UserIdAndRestaurant_RestaurantId(Integer userId, Integer restaurantId);
    List<EvaluationEntity> findByRestaurant_RestaurantIdAndStatus(Integer restaurantId, String status);

    Integer countAllByStatus(String status);

    Optional<EvaluationEntity> findByUserAndRestaurant(UserEntity user, RestaurantEntity restaurant);
    Optional<EvaluationEntity> findByUserAndRestaurantAndStatus(UserEntity user, RestaurantEntity restaurant, String status);

    Integer countByRestaurant(RestaurantEntity restaurant);

    Optional<EvaluationEntity> findByEvaluationIdAndStatus(Integer evaluationId, String status);

    List<EvaluationEntity> findByStatus(String status);
}
