package com.kustaurant.restauranttier.tab4_community.repository;

import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostScrap;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostScrapApiRepository extends JpaRepository<PostScrap, Integer> {
    Optional<PostScrap> findByUserAndPost(User user, Post post);
}
