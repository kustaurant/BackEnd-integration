package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostDislike;
import com.kustaurant.kustaurant.common.post.service.port.PostDislikeRepository;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepositoryImpl;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostDislikeRepositoryImpl implements PostDislikeRepository {
    private final PostDislikeJpaRepository postDislikeJpaRepository;


    @Override
    public Optional<PostDislike> findByUserIdAndPostId(Integer userId, Integer postId) {

        return postDislikeJpaRepository.findByUserIdAndPostId(userId, postId)
                .map(PostDislikeEntity::toDomain);
    }

    @Override
    public boolean existsByUserIdAndPostId(Integer userId, Integer postId) {
        return postDislikeJpaRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public PostDislike save(PostDislike postDislike) {
        return postDislikeJpaRepository.save(PostDislikeEntity.from(postDislike)).toDomain();
    }

    @Override
    public void deleteByUserIdAndPostId(Integer userId, Integer postId) {
        postDislikeJpaRepository.findByUserIdAndPostId(userId, postId)
                .ifPresent(postDislikeJpaRepository::delete);
    }

}
