package com.kustaurant.kustaurant.post.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostDislikeJpaRepository extends JpaRepository<PostDislikeEntity,Integer> {
    Optional<PostDislikeEntity> findByUser_UserIdAndPost_PostId(Long userId, Integer postId);

    boolean existsByUser_UserIdAndPost_PostId(Long userId, Integer postId);

    int countByPost_PostId(Integer postId);
}
