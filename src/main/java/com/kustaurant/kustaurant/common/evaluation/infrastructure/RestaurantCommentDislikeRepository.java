package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation.EvaluationEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentDislikeRepository extends JpaRepository<RestaurantCommentDislike, Integer> {
    Optional<RestaurantCommentDislike> findByUserAndRestaurantComment(UserEntity UserEntity, RestaurantComment restaurantComment);

    Optional<RestaurantCommentDislike> findByUserAndEvaluation(UserEntity UserEntity, EvaluationEntity evaluation);
}
