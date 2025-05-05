package com.kustaurant.kustaurant.common.post.service.port;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostScrap;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.post.infrastructure.PostScrapEntity;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostScrapRepository {
    List<PostScrap> findByUserId(Integer userId);

    Optional<PostScrapEntity> findByPostAndUser(PostEntity post, UserEntity user);

    List<PostScrapEntity> findActiveScrappedPostsByUserId(@Param("userId") Integer userId);

    boolean existsByUserAndPost(User user, Post post);

    void delete(PostScrap postScrap);
    void save(PostScrap postScrap);
}
