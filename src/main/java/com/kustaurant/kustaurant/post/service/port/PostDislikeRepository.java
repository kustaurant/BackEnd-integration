package com.kustaurant.kustaurant.post.service.port;

import com.kustaurant.kustaurant.post.domain.PostDislike;

import java.util.Optional;

public interface PostDislikeRepository {
    Optional<PostDislike> findByUserIdAndPostId(Integer userId, Integer postId);
    boolean existsByUserIdAndPostId(Integer userId, Integer postId);
    PostDislike save(PostDislike postDislike);
    void deleteByUserIdAndPostId(Integer userId, Integer postId);

    int countByPostId(Integer postId);
}
