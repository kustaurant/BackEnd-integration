package com.kustaurant.mainapp.post.comment.infrastructure;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.post.comment.domain.PostCommentReaction;
import com.kustaurant.mainapp.post.comment.domain.PostCommentReactionId;
import com.kustaurant.mainapp.post.comment.infrastructure.entity.PostCommentReactionJpaId;
import com.kustaurant.mainapp.post.comment.infrastructure.entity.PostCommentReactionEntity;
import com.kustaurant.mainapp.post.comment.infrastructure.jpa.PostCommentReactionJpaRepository;
import com.kustaurant.mainapp.post.comment.service.port.PostCommentReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PostCommentReactionRepositoryImpl implements PostCommentReactionRepository {
    private final PostCommentReactionJpaRepository jpa;

    @Override
    public Optional<PostCommentReaction> findById(PostCommentReactionId id) {
        PostCommentReactionJpaId jpaId = new PostCommentReactionJpaId(id.postCommentId(), id.userId());
        return jpa.findById(jpaId).map(PostCommentReactionEntity::toModel);
    }

    @Override
    public PostCommentReaction save(PostCommentReaction postCommentReaction) {
        return jpa.save(PostCommentReactionEntity.from(postCommentReaction)).toModel();
    }

    @Override
    public void deleteById(PostCommentReactionId id) {
        PostCommentReactionJpaId jpaId = new PostCommentReactionJpaId(id.postCommentId(), id.userId());
        jpa.deleteById(jpaId);
    }

    @Override
    public int countByPostCommentIdAndReaction(Long postCommentId, ReactionType reaction) {
        return jpa.countByIdPostCommentIdAndReaction(postCommentId, reaction);
    }

    @Override
    public void deleteByPostId(Long postId) {
        jpa.deleteByPostId(postId);
    }
}
