package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantCommentRepository extends JpaRepository<RestaurantComment, Long> {

    Optional<RestaurantComment> findByCommentId(Integer commentId);

    Optional<RestaurantComment> findByCommentIdAndStatus(Integer commentId, String active);

}