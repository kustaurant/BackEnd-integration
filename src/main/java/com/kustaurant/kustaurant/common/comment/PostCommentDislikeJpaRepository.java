package com.kustaurant.kustaurant.common.comment;

import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentDislikeJpaRepository extends JpaRepository<PostCommentDislikeEntity,Integer> {
    Optional<PostCommentDislikeEntity> findByUserAndPostComment(User user, PostComment postComment);

    boolean existsByUserAndPostComment(User user, PostComment postComment);
}
