package com.kustaurant.kustaurant.post.post.infrastructure.jpa;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostReactionJpaId;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostScrapEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostScrapJpaRepository extends JpaRepository<PostScrapEntity, PostReactionJpaId> {
    void deleteByIdPostId(Long postId);
    
    int countByIdPostId(Long postId);
}
