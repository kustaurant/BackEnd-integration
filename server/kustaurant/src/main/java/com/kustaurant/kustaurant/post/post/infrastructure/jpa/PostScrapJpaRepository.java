package com.kustaurant.kustaurant.post.post.infrastructure.jpa;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostReactionJpaId;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostScrapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PostScrapJpaRepository extends JpaRepository<PostScrapEntity, PostReactionJpaId> {
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    boolean existsById(PostReactionJpaId postReactionJpaId);

    void deleteByIdPostId(Long postId);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    int countByIdPostId(Long postId);
}
