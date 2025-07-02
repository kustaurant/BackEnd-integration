package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentDislikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RestaurantCommentDislikeRepository extends JpaRepository<RestaurantCommentDislikeEntity, Integer> {
    @Query("select d from RestaurantCommentDislikeEntity d "
            + "where d.userId = :userId and d.restaurantComment.commentId = :commentId")
    Optional<RestaurantCommentDislikeEntity> findByUserIdAndRestaurantCommentId(@Param("userId") Long userId, @Param("commentId") Integer commentId);

    @Query("select d from RestaurantCommentDislikeEntity d "
            + "where d.userId = :userId and d.evaluation.id = :evaluationId")
    Optional<RestaurantCommentDislikeEntity> findByUserIdAndEvaluationId(@Param("userId") Long userId, @Param("evaluationId") Integer evaluationId);
}
