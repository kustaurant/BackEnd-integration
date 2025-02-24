package com.kustaurant.restauranttier.tab4_community.repository;

import com.kustaurant.restauranttier.tab4_community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAll(Specification<Post> spec, Pageable pageable);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByStatus(String status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.userId = :userId AND p.status = 'ACTIVE'")
    List<Post> findActivePostsByUserId(@Param("userId") Integer userId);
}
