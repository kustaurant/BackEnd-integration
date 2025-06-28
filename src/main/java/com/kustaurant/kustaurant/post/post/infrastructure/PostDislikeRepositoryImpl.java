package com.kustaurant.kustaurant.post.post.infrastructure;

import com.kustaurant.kustaurant.post.post.domain.PostDislike;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostDislikeEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.repositoryInterface.PostDislikeJpaRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostDislikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostDislikeRepositoryImpl implements PostDislikeRepository {
    private final PostDislikeJpaRepository postDislikeJpaRepository;


    @Override
    public Optional<PostDislike> findByUserIdAndPostId(Long userId, Integer postId) {

        return postDislikeJpaRepository.findByUserIdAndPost_PostId(userId, postId)
                .map(PostDislikeEntity::toDomain);
    }

    @Override
    public boolean existsByUserIdAndPostId(Long userId, Integer postId) {
        return postDislikeJpaRepository.existsByUserIdAndPost_PostId(userId, postId);
    }

    @Override
    public PostDislike save(PostDislike postDislike) {
        return postDislikeJpaRepository.save(PostDislikeEntity.from(postDislike)).toDomain();
    }

    @Override
    public void deleteByUserIdAndPostId(Long userId, Integer postId) {
        postDislikeJpaRepository.findByUserIdAndPost_PostId(userId, postId)
                .ifPresent(postDislikeJpaRepository::delete);
    }

    @Override
    public int countByPostId(Integer postId) {
        return postDislikeJpaRepository.countByPost_PostId(postId);
    }

}
