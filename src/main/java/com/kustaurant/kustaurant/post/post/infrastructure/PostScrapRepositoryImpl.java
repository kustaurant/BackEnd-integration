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
    private final PostScrapJpaRepository postScrapJpaRepository;

    @Override
    public Optional<PostScrap> findById(PostReactionId id) {
        PostReactionJpaId jpaId = new PostReactionJpaId(id.postId(), id.userId());
        return postScrapJpaRepository.findById(jpaId).map(PostScrapEntity::toModel);
    }

    @Override
    public void save(PostScrap postScrap) {
        postScrapJpaRepository.save(PostScrapEntity.from(postScrap));
    }

    @Override
    public int countByPostId(Long postId) {
        return postScrapJpaRepository.countByIdPostId(postId);
    }

    @Override
    public void delete(PostScrap postScrap) {
        PostReactionJpaId jpaId = new PostReactionJpaId(postScrap.getId().postId(), postScrap.getId().userId());
        postScrapJpaRepository.findById(jpaId)
                .ifPresent(postScrapJpaRepository::delete);
    }

    @Override
    public void deleteByPostId(Long postId) {
        postScrapJpaRepository.deleteByIdPostId(postId);
    }

}
