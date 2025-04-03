package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity,Integer> {
    Optional<PostLikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

    Boolean existsByUserAndPost(UserEntity user, PostEntity post);
}
