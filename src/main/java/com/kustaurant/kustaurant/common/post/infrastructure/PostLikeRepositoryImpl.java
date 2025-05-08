package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostLike;
import com.kustaurant.kustaurant.common.post.service.port.PostLikeRepository;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepositoryImpl;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
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
                .findByUserIdAndPostId(userId,postId)
                .map(PostLikeEntity::toDomain);
    }

    @Override
    public Boolean existsByUserIdAndPostId(Integer userId, Integer postId) {
        return postLikeJpaRepository.existsByUserIdAndPostId(userId,postId);
    }

    @Override
    public void save(PostLike postLike) {
        PostLikeEntity entity = PostLikeEntity.from(postLike);
        postLikeJpaRepository.save(entity);
    }

    @Override
    public void deleteByUserIdAndPostId(Integer userId, Integer postId) {
        postLikeJpaRepository.findByUserIdAndPostId(userId,postId)
                .ifPresent(postLikeJpaRepository::delete);
    }

}
