package com.kustaurant.kustaurant.post.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity,Integer> {
    Optional<PostLikeEntity> findByUser_UserIdAndPost_PostId(Long userId, Integer postId);

    Boolean existsByUser_UserIdAndPost_PostId(Long userId, Integer postId);

    int countByPost_PostId(Integer postId);
}
