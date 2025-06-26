package com.kustaurant.kustaurant.evaluation.infrastructure;

import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentLikeRepository extends JpaRepository<RestaurantCommentLike, Integer> {
    Optional<RestaurantCommentLike> findByUserAndRestaurantComment(UserEntity UserEntity, RestaurantComment restaurantComment);

    Optional<RestaurantCommentLike> findByUserAndEvaluation(UserEntity UserEntity, EvaluationEntity evaluation);
}
