package com.kustaurant.kustaurant.post.post.service.port;

import com.kustaurant.kustaurant.post.post.domain.PostScrap;

import java.util.List;
import java.util.Optional;

public interface PostScrapRepository {
    void delete(PostScrap postScrap);

    void deleteByPostId(Long postId);

    Optional<PostScrap> findByUserIdAndPostId(Long userId,Long postId);

    void save(PostScrap postScrap);
    
    int countByPostId(Long postId);
}
