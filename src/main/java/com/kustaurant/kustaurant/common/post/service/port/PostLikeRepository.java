package com.kustaurant.kustaurant.common.post.service.port;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostDislike;
import com.kustaurant.kustaurant.common.post.domain.PostLike;
import com.kustaurant.kustaurant.common.user.domain.User;

import java.util.Optional;

public interface PostLikeRepository {
    Optional<PostLike> findByUserIdAndPostId(Integer userId, Integer postId);

    Boolean existsByUserIdAndPostId(Integer userId, Integer postId);

    void save(PostLike postLike);

    void deleteByUserIdAndPostId(Integer userId, Integer postId);
}
