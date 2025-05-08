package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentDislikeJpaRepository extends JpaRepository<PostCommentDislikeEntity,Integer> {
    Optional<PostCommentDislikeEntity> findByUserIdAndCommentId(Integer userId, Integer commentId);

    boolean existsByUserIdAndCommentId(Integer userId, Integer commentId);
}
