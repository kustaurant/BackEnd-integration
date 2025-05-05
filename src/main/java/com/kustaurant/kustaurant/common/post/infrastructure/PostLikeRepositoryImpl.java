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
        PostEntity postEntity = postRepositoryImpl.findEntityById(post.getPostId())
                .orElseThrow(() -> new DataNotFoundException("페이지를 찾을 수 없습니다."));

        UserEntity userEntity = userRepositoryImpl.findEntityById(user.getId())
                .orElseThrow(() -> new DataNotFoundException("유저를 찾을 수 없습니다."));

        return postLikeJpaRepository.findByUserAndPost(userEntity,postEntity);
    }

    @Override
    public Boolean existsByUserAndPost(User user, Post post) {
        return null;
    }
}
