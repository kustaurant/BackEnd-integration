package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.PostLike;
import com.kustaurant.kustaurant.common.post.service.port.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepository {
    private final PostLikeJpaRepository postLikeJpaRepository;

    @Override
    public Optional<PostLike> findByUserIdAndPostId(Integer userId, Integer postId) {
        return postLikeJpaRepository
                .findByUser_UserIdAndPost_PostId(userId,postId)
                .map(PostLikeEntity::toDomain);
    }

    @Override
    public Boolean existsByUserIdAndPostId(Integer userId, Integer postId) {
        return postLikeJpaRepository.existsByUser_UserIdAndPost_PostId(userId,postId);
    }

    @Override
    public void save(PostLike postLike) {
        PostLikeEntity entity = PostLikeEntity.from(postLike);
        postLikeJpaRepository.save(entity);
    }

    @Override
    public void deleteByUserIdAndPostId(Integer userId, Integer postId) {
        postLikeJpaRepository.findByUser_UserIdAndPost_PostId(userId,postId)
                .ifPresent(entity -> {
                    postLikeJpaRepository.delete(entity);
                    postLikeJpaRepository.flush(); // 즉시 DB 반영
                });
    }

    @Override
    public int countByPostId(Integer postId) {
        return postLikeJpaRepository.countByPost_PostId(postId);
    }


}
