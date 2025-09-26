package com.kustaurant.kustaurant.post.post.infrastructure;

import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import com.kustaurant.kustaurant.post.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostReactionJpaId;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostScrapEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.jpa.PostScrapJpaRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostScrapRepositoryImpl implements PostScrapRepository {
    private final PostScrapJpaRepository jpa;

    @Override
    public Optional<PostScrap> findById(PostReactionId id) {
        PostReactionJpaId jpaId = new PostReactionJpaId(id.postId(), id.userId());
        return jpa.findById(jpaId).map(PostScrapEntity::toModel);
    }

    @Override
    public boolean existsById(PostReactionId id) {
        PostReactionJpaId jpaId = new PostReactionJpaId(id.postId(), id.userId());
        return jpa.existsById(jpaId);
    }

    @Override
    public void save(PostScrap postScrap) {
        jpa.save(PostScrapEntity.from(postScrap));
    }

    @Override
    public int countByPostId(Long postId) {
        return jpa.countByIdPostId(postId);
    }

    @Override
    public void deleteById(PostReactionId id) {
        PostReactionJpaId jpaId = new PostReactionJpaId(id.postId(), id.userId());
        jpa.deleteById(jpaId);
    }

    @Override
    public void deleteByPostId(Long postId) {
        jpa.deleteByIdPostId(postId);
    }

}
