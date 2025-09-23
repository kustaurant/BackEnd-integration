package com.kustaurant.mainapp.post.post.infrastructure.jpa;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.post.post.infrastructure.entity.PostReactionEntity;
import com.kustaurant.mainapp.post.post.infrastructure.entity.PostReactionJpaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PostReactionJpaRepository extends JpaRepository<PostReactionEntity, PostReactionJpaId> {

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<PostReactionEntity> findById(PostReactionJpaId postReactionJpaId);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    int countByIdPostIdAndReaction(Long postId, ReactionType reaction);

    void deleteByIdPostId(Long postId);
}
