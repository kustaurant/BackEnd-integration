package com.kustaurant.kustaurant.common.comment;

import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentLikeJpaRepository extends JpaRepository<PostCommentLikeEntity,Integer> {
    Optional<PostCommentLikeEntity> findByPostCommentAndUser(PostComment postComment, User user);

    boolean existsByPostCommentAndUser(User user, PostComment postComment);
}
