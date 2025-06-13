package com.kustaurant.kustaurant.post.service.port;

import com.kustaurant.kustaurant.post.domain.PostScrap;

import java.util.List;
import java.util.Optional;

public interface PostScrapRepository {
    List<PostScrap> findByUserId(Integer userId);

    boolean existsByUserIdAndPostId(Integer userId, Integer postId);

    void delete(PostScrap postScrap);

    void deleteByPostId(Integer postId);

    Optional<PostScrap> findByUserIdAndPostId(Integer userId,Integer postId);

    void save(PostScrap postScrap);
}
