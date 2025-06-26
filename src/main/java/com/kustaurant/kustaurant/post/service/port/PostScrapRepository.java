package com.kustaurant.kustaurant.post.service.port;

import com.kustaurant.kustaurant.post.domain.PostScrap;
import com.kustaurant.kustaurant.user.mypage.controller.response.ScrappedPostView;

import java.util.List;
import java.util.Optional;

public interface PostScrapRepository {
    List<PostScrap> findByUserId(Long userId);

    boolean existsByUserIdAndPostId(Long userId, Integer postId);

    void delete(PostScrap postScrap);

    void deleteByPostId(Integer postId);

    Optional<PostScrap> findByUserIdAndPostId(Long userId,Integer postId);

    void save(PostScrap postScrap);

    public List<ScrappedPostView> findScrapViewsByUserId(Long userId);
}
