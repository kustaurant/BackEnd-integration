package com.kustaurant.mainapp.post.comment.infrastructure.jpa;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.post.comment.infrastructure.entity.PostCommentReactionEntity;
import com.kustaurant.mainapp.post.comment.infrastructure.entity.PostCommentReactionJpaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostCommentReactionJpaRepository extends JpaRepository<PostCommentReactionEntity, PostCommentReactionJpaId> {

    int countByIdPostCommentIdAndReaction(Long postCommentId, ReactionType reaction);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from PostCommentReactionEntity r
        where r.id.postCommentId in (
            select c.postCommentId from PostCommentEntity c
            where c.postId = :postId
        )
    """)
    void deleteByPostId(Long postId);
}
