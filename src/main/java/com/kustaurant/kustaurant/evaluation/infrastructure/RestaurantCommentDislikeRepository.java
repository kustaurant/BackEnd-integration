package com.kustaurant.kustaurant.evaluation.infrastructure;

import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentDislikeRepository extends JpaRepository<RestaurantCommentDislike, Integer> {
    Optional<RestaurantCommentDislike> findByUserAndRestaurantComment(Long userId, RestaurantComment restaurantComment);

    Optional<RestaurantCommentDislike> findByUserAndEvaluation(Long userId, EvaluationEntity evaluation);
}
