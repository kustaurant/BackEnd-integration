package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostScrapApiRepository extends JpaRepository<PostScrapEntity, Integer> {
    Optional<PostScrapEntity> findByUserAndPost(UserEntity UserEntity, PostEntity post);
}
