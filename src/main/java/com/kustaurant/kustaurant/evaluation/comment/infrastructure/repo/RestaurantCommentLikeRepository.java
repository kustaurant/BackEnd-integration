package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentLikeEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentLikeRepository extends JpaRepository<RestaurantCommentLikeEntity, Integer> {
    Optional<RestaurantCommentLikeEntity> findByUserIdAndRestaurantComment(Long userId, RestaurantCommentEntity restaurantComment);

    Optional<RestaurantCommentLikeEntity> findByUserIdAndEvaluation(Long userId, EvaluationEntity evaluation);
}
