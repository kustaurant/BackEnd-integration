package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.Evaluation;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantCommentDislike;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantCommentLike;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentDislikeRepository extends JpaRepository<RestaurantCommentDislike, Integer> {
    Optional<RestaurantCommentDislike> findByUserAndRestaurantComment(User user, RestaurantComment restaurantComment);

    Optional<RestaurantCommentDislike> findByUserAndEvaluation(User user, Evaluation evaluation);
}
