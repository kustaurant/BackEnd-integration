package com.kustaurant.kustaurant.common.post.service.port;

import com.kustaurant.kustaurant.common.post.domain.PostScrap;

import java.util.List;

public interface PostScrapRepository {
    List<PostScrap> findByUserId(Integer userId);
}
