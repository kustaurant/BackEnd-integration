package com.kustaurant.kustaurant.post.service.port;

import com.kustaurant.kustaurant.post.domain.PostLike;

import java.util.Optional;

public interface PostLikeRepository {
    Optional<PostLike> findByUserIdAndPostId(Integer userId, Integer postId);

    Boolean existsByUserIdAndPostId(Integer userId, Integer postId);

    void save(PostLike postLike);

    void deleteByUserIdAndPostId(Integer userId, Integer postId);

    int countByPostId(Integer postId);
}
