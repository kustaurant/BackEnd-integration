package com.kustaurant.kustaurant.common.post.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostApiRepository extends JpaRepository<PostEntity, Integer> {
    Optional<PostEntity> findByStatusAndPostId(String status, Integer postId);

    Page<PostEntity> findAll(Specification<PostEntity> spec, Pageable pageable);

    Page<PostEntity> findAll(Pageable pageable);

    Page<PostEntity> findByStatus(String status, Pageable pageable);

}
