package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentLikeJpaRepository extends JpaRepository<PostCommentLikeEntity,Integer> {
    Optional<PostCommentLikeEntity> findByUserAndPostComment(UserEntity user, PostCommentEntity postComment);

    boolean existsByUserAndPostComment(UserEntity user, PostCommentEntity postComment);
}
