package com.kustaurant.kustaurant.post.comment.service.port;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentReaction;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentReactionId;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentReactionJpaId;

import java.util.Optional;

public interface PostCommentReactionRepository {
    Optional<PostCommentReaction> findById(PostCommentReactionId id);
    PostCommentReaction save(PostCommentReaction postCommentReaction);
    void deleteById(PostCommentReactionId id);
    int countByPostCommentIdAndReaction(Long postCommentId, ReactionType reaction);
    void deleteByPostId(Long postId);
}
