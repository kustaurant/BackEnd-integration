package com.kustaurant.mainapp.post.comment.service.port;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.post.comment.domain.PostCommentReaction;
import com.kustaurant.mainapp.post.comment.domain.PostCommentReactionId;
import com.kustaurant.mainapp.post.comment.infrastructure.entity.PostCommentReactionJpaId;

import java.util.Optional;

public interface PostCommentReactionRepository {
    Optional<PostCommentReaction> findById(PostCommentReactionId id);
    PostCommentReaction save(PostCommentReaction postCommentReaction);
    void deleteById(PostCommentReactionId id);
    int countByPostCommentIdAndReaction(Long postCommentId, ReactionType reaction);
    void deleteByPostId(Long postId);
}
