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
        return postDislikeJpaRepository.findByUserIdAndPostId(userId, postId)
                .map(PostDislikeEntity::toDomain);
    }

    @Override
    public Boolean existsByUserIdAndPostId(Long userId, Integer postId) {
        return postDislikeJpaRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public void save(PostDislike postDislike) {
        postDislikeJpaRepository.save(PostDislikeEntity.from(postDislike)).toDomain();
    }

    @Override
    public void deleteByUserIdAndPostId(Long userId, Integer postId) {
        postDislikeJpaRepository.findByUserIdAndPostId(userId, postId)
                .ifPresent(postDislikeJpaRepository::delete);
    }

    @Override
    public int countByPostId(Integer postId) {
        return postDislikeJpaRepository.countByPostId(postId);
    }

}
