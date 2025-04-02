package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostDislikeJpaRepository extends JpaRepository<PostDislikeEntity,Integer> {
    Optional<PostDislikeEntity> findByUserAndPostEntity(UserEntity user, PostEntity postEntity);

    boolean existsByUserAndPostEntity(UserEntity user, PostEntity postEntity);
}
