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
    private final UserRepositoryImpl userRepositoryImpl;
    private final PostRepositoryImpl postRepositoryImpl;

    @Override
    public Optional<PostLike> findByUserAndPost(User user, Post post) {
        Optional<PostEntity> postEntityOpt = postRepositoryImpl.findEntityById(post.getId());
        Optional<UserEntity> userEntityOpt = userRepositoryImpl.findEntityById(user.getId());

        if (postEntityOpt.isEmpty() || userEntityOpt.isEmpty()) {
            return Optional.empty();
        }

        return postLikeJpaRepository
                .findByUserAndPost(userEntityOpt.get(), postEntityOpt.get())
                .map(PostLikeEntity::toDomain);
    }

    @Override
    public Boolean existsByUserAndPost(User user, Post post) {
        return null;
    }


    @Override
    public void save(PostLike postLike) {
        UserEntity userEntity = userRepositoryImpl.findEntityById(postLike.getUser().getId())
                .orElseThrow(() -> new DataNotFoundException("유저가 존재하지 않습니다"));

        PostEntity postEntity = postRepositoryImpl.findEntityById(postLike.getPost().getId())
                .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다"));

        PostLikeEntity entity = PostLikeEntity.from(postLike, userEntity, postEntity);
        postLikeJpaRepository.save(entity);
    }

    @Override
    public void deleteByUserAndPost(User user, Post post) {
        UserEntity userEntity = userRepositoryImpl.findEntityById(user.getId())
                .orElseThrow(() -> new DataNotFoundException("유저가 존재하지 않습니다"));

        PostEntity postEntity = postRepositoryImpl.findEntityById(post.getId())
                .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다"));

        postLikeJpaRepository.findByUserAndPost(userEntity, postEntity)
                .ifPresent(postLikeJpaRepository::delete);
    }

}
