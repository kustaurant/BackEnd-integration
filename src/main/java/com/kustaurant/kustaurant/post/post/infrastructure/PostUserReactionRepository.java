package com.kustaurant.kustaurant.post.post.infrastructure;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostUserReactionEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostUserReactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostUserReactionRepository extends JpaRepository<PostUserReactionEntity, PostUserReactionId> {
    int countByPostIdAndReaction(Integer postId, ReactionType reaction);
}
