package com.kustaurant.kustaurant.post.post.service.port;

import com.kustaurant.kustaurant.post.post.domain.PostDislike;

import java.util.Optional;

public interface PostDislikeRepository {
    Optional<PostDislike> findByUserIdAndPostId(Long userId, Integer postId);
    boolean existsByUserIdAndPostId(Long userId, Integer postId);
    PostDislike save(PostDislike postDislike);
    void deleteByUserIdAndPostId(Long userId, Integer postId);

    int countByPostId(Integer postId);
}
