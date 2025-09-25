package com.kustaurant.kustaurant.post.post.service.port;

import com.kustaurant.kustaurant.post.post.domain.Post;

import java.util.Optional;

public interface PostRepository {
    Optional<Post> findById(Long id);
    Post save(Post post);
    void delete(Long id);
    void increaseVisitCount(Long postId);
}