package com.kustaurant.restauranttier.tab4_community.repository;

import com.kustaurant.restauranttier.tab4_community.entity.PostComment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment,Integer> {
    List<PostComment> findAll(Specification<PostComment> spec);

    @Query("SELECT pc FROM PostComment pc WHERE pc.user.userId = :userId AND pc.post.status = 'ACTIVE'")
    List<PostComment> findActiveCommentedPostsByUserId(@Param("userId") Integer userId);
}
