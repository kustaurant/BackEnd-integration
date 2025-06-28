package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentDislikeEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentDislikeRepository extends JpaRepository<RestaurantCommentDislikeEntity, Integer> {
    Optional<RestaurantCommentDislikeEntity> findByUserIdAndRestaurantComment(Long userId, RestaurantCommentEntity restaurantComment);

    Optional<RestaurantCommentDislikeEntity> findByUserIdAndEvaluation(Long userId, EvaluationEntity evaluation);
}
