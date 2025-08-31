package com.kustaurant.kustaurant.post.post.infrastructure.jpa;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostReactionEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostReactionJpaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReactionJpaRepository extends JpaRepository<PostReactionEntity, PostReactionJpaId> {
    int countByIdPostIdAndReaction(Long postId, ReactionType reaction);

    void deleteByIdPostId(Long postId);
}
