package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostDislikesJpaRepository extends JpaRepository<PostDislikesEntity,Integer> {
    Optional<PostDislikesEntity> findByUserAndPostEntity(User user, PostEntity postEntity);

    boolean existsByUserAndPostEntity(User user, PostEntity postEntity);
}
