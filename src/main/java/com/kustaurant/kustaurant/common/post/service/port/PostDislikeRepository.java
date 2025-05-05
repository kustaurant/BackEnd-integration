package com.kustaurant.kustaurant.common.post.service.port;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostDislike;
import com.kustaurant.kustaurant.common.post.infrastructure.PostDislikeEntity;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.post.infrastructure.PostLikeEntity;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;

import java.util.Optional;

public interface PostDislikeRepository {
    Optional<PostDislike> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    PostDislike save(PostDislike postDislike);
    PostDislike delete(PostDislike postDislike);

}
