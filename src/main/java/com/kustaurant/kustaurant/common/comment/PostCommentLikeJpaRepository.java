package com.kustaurant.kustaurant.common.comment;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentLikeJpaRepository extends JpaRepository<PostCommentLikeEntity,Integer> {
    Optional<PostCommentLikeEntity> findByUserAndPostComment(UserEntity user, PostComment postComment);

    boolean existsByUserAndPostComment(UserEntity user, PostComment postComment);
}
