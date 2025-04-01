package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikesJpaRepository extends JpaRepository<PostLikesEntity,Integer> {
    Optional<PostLikesEntity> findByUserAndPostEntity(User user, PostEntity postEntity);

    Boolean existsByUserAndPostEntity(User user, PostEntity postEntity);
}
