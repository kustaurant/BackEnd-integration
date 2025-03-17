package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Evaluation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantComment;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantCommentLike;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentLikeRepository extends JpaRepository<RestaurantCommentLike, Integer> {
    Optional<RestaurantCommentLike> findByUserAndRestaurantComment(User user, RestaurantComment restaurantComment);

    Optional<RestaurantCommentLike> findByUserAndEvaluation(User user, Evaluation evaluation);
}
