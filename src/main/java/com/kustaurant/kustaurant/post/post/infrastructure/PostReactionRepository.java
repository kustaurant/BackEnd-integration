package com.kustaurant.kustaurant.post.post.infrastructure;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostReactionEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostUserReactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReactionRepository extends JpaRepository<PostReactionEntity, PostUserReactionId> {
    int countByPostIdAndReaction(Long postId, ReactionType reaction);

    void deleteByPostId(Long postId);
}
