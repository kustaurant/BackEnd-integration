package com.kustaurant.kustaurant.common.comment;

import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.post.infrastructure.PostLikesEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentLikesJpaRepository extends JpaRepository<PostCommentLikesEntity,Integer> {
    Optional<PostCommentLikesEntity> findByPostCommentAndUser(PostComment postComment, User user);

}
