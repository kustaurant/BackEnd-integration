package com.kustaurant.kustaurant.post.comment.infrastructure.jpa;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentReactionEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommUserReactionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostCommentReactionRepository extends JpaRepository<PostCommentReactionEntity, PostCommUserReactionId> {

    int countByPostCommentIdAndReaction(Integer postCommentId, ReactionType reaction);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from PostCommentReactionEntity r
        where r.postCommentId in (
            select c.commentId from PostCommentEntity c
            where c.postId = :postId
        )
    """)
    void deleteByPostId(Long postId);
}
