package com.kustaurant.restauranttier.tab4_community.repository;

import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostScrap;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Integer> {
    Optional<PostScrap> findByUserAndPost(User user, Post post);

    @Query("SELECT ps FROM PostScrap ps WHERE ps.user.userId = :userId AND ps.post.status = 'ACTIVE'")
    List<PostScrap> findActiveScrappedPostsByUserId(@Param("userId") Integer userId);
}
