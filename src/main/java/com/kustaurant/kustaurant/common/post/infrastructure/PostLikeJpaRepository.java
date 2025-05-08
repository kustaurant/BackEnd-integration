package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity,Integer> {
    Optional<PostLikeEntity> findByUserIdAndPostId(Integer userId, Integer postId);

    Boolean existsByUserIdAndPostId(Integer userId, Integer postId);

}
