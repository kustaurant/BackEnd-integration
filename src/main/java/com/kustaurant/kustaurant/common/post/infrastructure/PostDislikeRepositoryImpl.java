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
    private final UserRepositoryImpl userRepositoryImpl;
    private final PostRepositoryImpl postRepositoryImpl;

    @Override
    public Optional<PostDislike> findByUserAndPost(User user, Post post) {
        PostEntity postEntity = postRepositoryImpl.findEntityById(post.getPostId())
                .orElseThrow(() -> new DataNotFoundException("페이지를 찾을 수 없습니다."));

        UserEntity userEntity = userRepositoryImpl.findEntityById(user.getId())
                .orElseThrow(() -> new DataNotFoundException("유저를 찾을 수 없습니다."));

        return postDislikeJpaRepository.findByUserAndPost(userEntity, postEntity)
                .map(PostDislikeEntity::toDomain);
    }

    @Override
    public boolean existsByUserAndPost(User user, Post post) {
        return postDislikeJpaRepository.existsByUserAndPost();
    }

    @Override
    public PostDislike delete(PostDislike postDislike) {
        PostDislikeEntity entity = PostDislikeEntity.from(postDislike);
        postDislikeJpaRepository.delete(entity);
        return postDislike;
    }

    @Override
    public void save(PostDislike postDislike) {
        UserEntity userEntity = userRepositoryImpl.findEntityById(postDislike.getUser().getId())
                .orElseThrow(() -> new DataNotFoundException("유저가 존재하지 않습니다"));

        PostEntity postEntity = postRepositoryImpl.findEntityById(postDislike.getPost().getId())
                .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다"));

        PostLikeEntity entity = PostDislikeEntity.from(postDislike, userEntity, postEntity);
        postDislikeJpaRepository.save(entity);
    }

    @Override
    public void deleteByUserAndPost(User user, Post post) {
        UserEntity userEntity = userRepositoryImpl.findEntityById(user.getId())
                .orElseThrow(() -> new DataNotFoundException("유저가 존재하지 않습니다"));

        PostEntity postEntity = postRepositoryImpl.findEntityById(post.getId())
                .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다"));

        postDislikeJpaRepository.findByUserAndPost(userEntity, postEntity)
                .ifPresent(postDislikeJpaRepository::delete);
    }

}
