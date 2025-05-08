package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentLikeJpaRepository extends JpaRepository<PostCommentLikeEntity, Integer> {
    Optional<PostCommentLikeEntity> findByUserIdAndCommentId(Integer userId, Integer commentId);

    boolean existsByUserIdAndCommentId(Integer userId, Integer commentId);
}
