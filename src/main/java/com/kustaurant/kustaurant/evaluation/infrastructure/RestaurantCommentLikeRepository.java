package com.kustaurant.kustaurant.evaluation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantCommentLikeRepository extends JpaRepository<RestaurantCommentLike, Integer> {

    @Query("select l from RestaurantCommentLike l "
            + "where l.user.userId = :userId and l.restaurantComment.commentId = :commentId")
    Optional<RestaurantCommentLike> findByUserIdAndRestaurantCommentId(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    @Query("select l from RestaurantCommentLike l "
            + "where l.user.userId = :userId and l.evaluation.evaluationId = :evaluationId")
    Optional<RestaurantCommentLike> findByUserIdAndEvaluationId(@Param("userId") Integer userId, @Param("evaluationId") Integer evaluationId);
}
