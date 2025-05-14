package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.PostScrap;
import com.kustaurant.kustaurant.common.post.service.port.PostScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostScrapRepositoryImpl implements PostScrapRepository {
    private final PostScrapJpaRepository postScrapJpaRepository;

    @Override
    public List<PostScrap> findByUserId(Integer userId) {
        return postScrapJpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(PostScrap::from).toList();
    }

    @Override
    public boolean existsByUserIdAndPostId(Integer userId, Integer postId) {
        return postScrapJpaRepository.existsByUser_UserIdAndPost_PostId(userId, postId);
    }

    @Override
    public void delete(PostScrap postScrap) {

    }

    @Override
    public void deleteByPostId(Integer postId) {
        postScrapJpaRepository.deleteByPost_PostId(postId);
    }

    @Override
    public Optional<PostScrap> findByUserIdAndPostId(Integer userId, Integer postId) {
        return postScrapJpaRepository.findByUser_UserIdAndPost_PostId(userId, postId).map(PostScrapEntity::toDomain);
    }

    @Override
    public void save(PostScrap postScrap) {
        PostScrapEntity postScrapEntity = PostScrapEntity.from(postScrap);
        postScrapJpaRepository.save(postScrapEntity);
    }
}
