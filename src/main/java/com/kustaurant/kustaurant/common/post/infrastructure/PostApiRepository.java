package com.kustaurant.kustaurant.common.post.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostApiRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findByStatusAndPostId(String status, Integer postId);

    Page<Post> findAll(Specification<Post> spec, Pageable pageable);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByStatus(String status, Pageable pageable);

}
