package com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationJpaRepository extends JpaRepository<EvaluationEntity, Integer>{
    Optional<EvaluationEntity> findByUser_UserIdAndRestaurant_RestaurantId(Integer userId, Integer restaurantId);
    List<EvaluationEntity> findByRestaurant_RestaurantIdAndStatus(Integer restaurantId, String status);

    Optional<EvaluationEntity> findByUserAndRestaurant(User user, RestaurantEntity restaurant);
    Optional<EvaluationEntity> findByUserAndRestaurantAndStatus(User user, RestaurantEntity restaurant, String status);

    Integer countByRestaurant(RestaurantEntity restaurant);

    Integer countAllByStatus(String status);

    Optional<EvaluationEntity> findByEvaluationIdAndStatus(Integer evaluationId, String status);

    List<EvaluationEntity> findByStatus(String status);
}
