package com.kustaurant.kustaurant.post.post.service.port;

import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import com.kustaurant.kustaurant.post.post.domain.PostScrap;

import java.util.Optional;

public interface PostScrapRepository {
    Optional<PostScrap> findById(PostReactionId id);
    void save(PostScrap postScrap);
    int countByPostId(Long postId);
    void delete(PostScrap postScrap);
    void deleteByPostId(Long postId);
}
