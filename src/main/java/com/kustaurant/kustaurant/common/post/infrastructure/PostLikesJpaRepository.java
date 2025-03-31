package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikesJpaRepository extends JpaRepository<PostLikesEntity,Integer> {
    Optional<PostLikesEntity> findByPostEntityAndUser(PostEntity postEntity, User user);

    Boolean existsByPostEntityAndUser(PostEntity postEntity, User user);
}
