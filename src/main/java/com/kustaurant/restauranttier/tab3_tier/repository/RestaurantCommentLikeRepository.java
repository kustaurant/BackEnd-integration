package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantCommentLike;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentLikeRepository extends JpaRepository<RestaurantCommentLike, Integer> {
    Optional<RestaurantCommentLike> findByUserAndRestaurantComment(User user, RestaurantComment restaurantComment);
}
