package com.kustaurant.kustaurant.evaluation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantCommentDislikeRepository extends JpaRepository<RestaurantCommentDislike, Integer> {
    @Query("select d from RestaurantCommentDislike d "
            + "where d.user.userId = :userId and d.restaurantComment.commentId = :commentId")
    Optional<RestaurantCommentDislike> findByUserIdAndRestaurantCommentId(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    @Query("select d from RestaurantCommentDislike d "
            + "where d.user.userId = :userId and d.evaluation.evaluationId = :evaluationId")
    Optional<RestaurantCommentDislike> findByUserIdAndEvaluationId(@Param("userId") Integer userId, @Param("evaluationId") Integer evaluationId);
}
