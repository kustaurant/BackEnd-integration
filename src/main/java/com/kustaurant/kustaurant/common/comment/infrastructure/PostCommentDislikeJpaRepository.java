package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentDislikeJpaRepository extends JpaRepository<PostCommentDislikeEntity,Integer> {
    Optional<PostCommentDislikeEntity> findByUserAndPostComment(UserEntity user, PostCommentEntity postComment);

    boolean existsByUserAndPostComment(UserEntity user, PostCommentEntity postComment);
}
