package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Evaluation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantComment;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantCommentDislike;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentDislikeRepository extends JpaRepository<RestaurantCommentDislike, Integer> {
    Optional<RestaurantCommentDislike> findByUserAndRestaurantComment(User user, RestaurantComment restaurantComment);

    Optional<RestaurantCommentDislike> findByUserAndEvaluation(User user, Evaluation evaluation);
}
