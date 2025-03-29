package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostDislikesJpaRepository extends JpaRepository<PostDislikesEntity,Integer> {
    Optional<PostDislikesEntity> findByPostEntityAndUser(PostEntity postEntity, User user);

    boolean existsByPostEntityAndUser(PostEntity post, User user);
}
