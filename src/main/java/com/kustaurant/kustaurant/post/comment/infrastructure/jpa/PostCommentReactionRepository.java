package com.kustaurant.kustaurant.post.comment.infrastructure.jpa;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentReactionEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommUserReactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentReactionRepository extends JpaRepository<PostCommentReactionEntity, PostCommUserReactionId> {

    int countByPostCommentIdAndReaction(Integer postCommentId, ReactionType reaction);
    void deleteByPostId(Integer postId);
}
