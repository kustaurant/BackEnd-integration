package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostDislikeJpaRepository extends JpaRepository<PostDislikeEntity,Integer> {
    Optional<PostDislikeEntity> findByUserAndPostEntity(User user, PostEntity postEntity);

    boolean existsByUserAndPostEntity(User user, PostEntity postEntity);
}
