package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostScrapApiRepository extends JpaRepository<PostScrap, Integer> {
    Optional<PostScrap> findByUserAndPost(User user, Post post);
}
