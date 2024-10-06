package com.kustaurant.restauranttier.tab4_community.repository;

import com.kustaurant.restauranttier.tab4_community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostApiRepository extends JpaRepository<Post,Integer> {
    Post findByPostTitle(String title);
    Optional<Post> findByStatusAndPostId(String status, Integer postId);
    Page<Post> findByPostCategory(String postCategory, Pageable pageable);

    Page<Post> findAll(Specification<Post> spec, Pageable pageable);
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByStatus(String status, Pageable pageable);

}
