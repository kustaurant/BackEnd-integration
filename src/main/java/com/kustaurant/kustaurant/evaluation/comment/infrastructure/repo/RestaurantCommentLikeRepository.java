package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RestaurantCommentLikeRepository extends JpaRepository<RestaurantCommentLikeEntity, Integer> {

    @Query("select l from RestaurantCommentLikeEntity l "
            + "where l.userId = :userId and l.restaurantComment.commentId = :commentId")
    Optional<RestaurantCommentLikeEntity> findByUserIdAndRestaurantCommentId(@Param("userId") Long userId, @Param("commentId") Integer commentId);

    @Query("select l from RestaurantCommentLikeEntity l "
            + "where l.userId = :userId and l.evaluation.id = :evaluationId")
    Optional<RestaurantCommentLikeEntity> findByUserIdAndEvaluationId(@Param("userId") Long userId, @Param("evaluationId") Integer evaluationId);
}
