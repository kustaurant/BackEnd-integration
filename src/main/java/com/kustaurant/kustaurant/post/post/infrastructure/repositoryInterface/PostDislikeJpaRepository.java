package com.kustaurant.kustaurant.post.post.infrastructure.repositoryInterface;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostDislikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostDislikeJpaRepository extends JpaRepository<PostDislikeEntity,Integer> {
    Optional<PostDislikeEntity> findByUserIdAndPost_PostId(Long userId, Integer postId);

    boolean existsByUserIdAndPost_PostId(Long userId, Integer postId);

    int countByPost_PostId(Integer postId);
}
