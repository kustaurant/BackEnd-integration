package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation.EvaluationEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentLikeRepository extends JpaRepository<RestaurantCommentLike, Integer> {
    Optional<RestaurantCommentLike> findByUserAndRestaurantComment(User user, RestaurantComment restaurantComment);

    Optional<RestaurantCommentLike> findByUserAndEvaluation(User user, EvaluationEntity evaluation);
}
