package com.kustaurant.kustaurant.common.post.service.port;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostDislike;
import com.kustaurant.kustaurant.common.post.domain.PostLike;
import com.kustaurant.kustaurant.common.user.domain.User;

import java.util.Optional;

public interface PostLikeRepository {
    Optional<PostLike> findByUserAndPost(User user, Post post);

    Boolean existsByUserAndPost(User user, Post post);

    void save(PostLike postLike);
    void deleteByUserAndPost(User user, Post post);

}
