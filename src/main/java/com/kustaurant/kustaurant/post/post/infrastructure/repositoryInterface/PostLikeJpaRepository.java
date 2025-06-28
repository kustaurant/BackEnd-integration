package com.kustaurant.kustaurant.post.post.infrastructure.repositoryInterface;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity,Integer> {
    Optional<PostLikeEntity> findByUserIdAndPost_PostId(Long userId, Integer postId);

    Boolean existsByUserIdAndPost_PostId(Long userId, Integer postId);

    int countByPost_PostId(Integer postId);
}
