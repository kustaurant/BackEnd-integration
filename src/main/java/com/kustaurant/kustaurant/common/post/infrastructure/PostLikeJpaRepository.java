package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity,Integer> {
    Optional<PostLikeEntity> findByUserAndPostEntity(UserEntity user, PostEntity postEntity);

    Boolean existsByUserAndPostEntity(UserEntity user, PostEntity postEntity);
}
