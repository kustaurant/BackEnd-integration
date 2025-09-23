package com.kustaurant.kustaurant.post.post.infrastructure;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.domain.PostReaction;
import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostReactionEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostReactionJpaId;
import com.kustaurant.kustaurant.post.post.infrastructure.jpa.PostReactionJpaRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostReactionRepositoryImpl implements PostReactionRepository {
    private final PostReactionJpaRepository jpa;

    @Override
    public Optional<PostReaction> findById(PostReactionId id) {
        PostReactionJpaId jpaId = new PostReactionJpaId(id.postId(), id.userId());
        return jpa.findById(jpaId).map(PostReactionEntity::toModel);
    }

    @Override
    public PostReaction save(PostReaction postReaction) {
        return jpa.save(PostReactionEntity.from(postReaction)).toModel();
    }

    @Override
    public void delete(PostReaction postReaction) {
        jpa.delete(PostReactionEntity.from(postReaction));
    }


    @Override
    public int countByPostIdAndReaction(Long postId, ReactionType reaction) {
        return jpa.countByIdPostIdAndReaction(postId, reaction);
    }

    @Override
    public void deleteByPostId(Long postId) {
        jpa.deleteByIdPostId(postId);
    }
}
