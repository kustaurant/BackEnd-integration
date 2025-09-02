package com.kustaurant.kustaurant.post.post.service.port;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.domain.PostReaction;
import com.kustaurant.kustaurant.post.post.domain.PostReactionId;

import java.util.Optional;

public interface PostReactionRepository {
    Optional<PostReaction> findById(PostReactionId id);
    PostReaction save(PostReaction postReaction);
    void delete(PostReactionId id);

    int countByPostIdAndReaction(Long postId, ReactionType reaction);
    void deleteByPostId(Long postId);

}
