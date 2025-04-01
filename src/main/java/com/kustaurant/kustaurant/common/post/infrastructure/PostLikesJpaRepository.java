package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikesJpaRepository extends JpaRepository<PostLikesEntity,Integer> {
    Optional<PostLikesEntity> findByPostEntityAndUser(PostEntity postEntity, UserEntity user);

    Boolean existsByPostEntityAndUser(PostEntity postEntity, UserEntity user);
}
