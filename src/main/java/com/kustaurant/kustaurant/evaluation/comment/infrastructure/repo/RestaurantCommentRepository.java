package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentRepository extends JpaRepository<RestaurantCommentEntity, Long> {

    Optional<RestaurantCommentEntity> findByCommentId(Integer commentId);

    Optional<RestaurantCommentEntity> findByCommentIdAndStatus(Integer commentId, String active);

}