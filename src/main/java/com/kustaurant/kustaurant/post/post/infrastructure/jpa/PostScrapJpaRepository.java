package com.kustaurant.kustaurant.post.post.infrastructure.jpa;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostScrapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostScrapJpaRepository extends JpaRepository<PostScrapEntity, Integer> {
    @Query("""
        SELECT ps FROM PostScrapEntity ps
        WHERE ps.userId = :userId
        ORDER BY ps.createdAt DESC""")
    List<PostScrapEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);


    void deleteByPostId(Long postId);

    Optional<PostScrapEntity> findByUserIdAndPostId(Long userId, Long postId);
    
    int countByPostId(Long postId);
}
