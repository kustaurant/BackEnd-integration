package com.kustaurant.kustaurant.post.post.infrastructure;

import com.kustaurant.kustaurant.post.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostScrapEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.repositoryInterface.PostScrapJpaRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostScrapRepositoryImpl implements PostScrapRepository {
    private final PostScrapJpaRepository postScrapJpaRepository;

    @Override
    public List<PostScrap> findByUserId(Long userId) {
        return postScrapJpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(PostScrap::from).toList();
    }

    @Override
    public boolean existsByUserIdAndPostId(Long userId, Integer postId) {
        return postScrapJpaRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public void delete(PostScrap postScrap) {
        postScrapJpaRepository.findByUserIdAndPostId(postScrap.getUserId(), postScrap.getPostId())
                .ifPresent(postScrapJpaRepository::delete);
    }


    @Override
    public void deleteByPostId(Integer postId) {
        postScrapJpaRepository.deleteByPostId(postId);
    }

    @Override
    public Optional<PostScrap> findByUserIdAndPostId(Long userId, Integer postId) {
        return postScrapJpaRepository.findByUserIdAndPostId(userId, postId).map(PostScrapEntity::toModel);
    }

    @Override
    public void save(PostScrap postScrap) {
        postScrapJpaRepository.save(PostScrapEntity.from(postScrap));
    }

    @Override
    public int countByPostId(Integer postId) {
        return postScrapJpaRepository.countByPostId(postId);
    }
}
