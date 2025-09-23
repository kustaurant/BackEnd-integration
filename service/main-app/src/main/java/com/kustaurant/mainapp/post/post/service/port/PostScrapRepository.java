package com.kustaurant.mainapp.post.post.service.port;

import com.kustaurant.mainapp.post.post.domain.PostReactionId;
import com.kustaurant.mainapp.post.post.domain.PostScrap;

import java.util.Optional;

public interface PostScrapRepository {
    Optional<PostScrap> findById(PostReactionId id);
    boolean existsById(PostReactionId id);
    void save(PostScrap postScrap);
    int countByPostId(Long postId);
    void deleteById(PostReactionId id);
    void deleteByPostId(Long postId);
}
