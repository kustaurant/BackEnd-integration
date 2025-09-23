package com.kustaurant.mainapp.post.post.service.port;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.post.post.domain.PostReaction;
import com.kustaurant.mainapp.post.post.domain.PostReactionId;

import java.util.Optional;

public interface PostReactionRepository {
    Optional<PostReaction> findById(PostReactionId id);
    PostReaction save(PostReaction postReaction);
    void delete(PostReaction postReaction);

    int countByPostIdAndReaction(Long postId, ReactionType reaction);
    void deleteByPostId(Long postId);

}
