package com.kustaurant.kustaurant.post.infrastructure;

import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostScrapApiRepository extends JpaRepository<PostScrapEntity, Integer> {
    Optional<PostScrapEntity> findByUserAndPost(Long userId, PostEntity post);
}
