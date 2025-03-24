package com.kustaurant.kustaurant.common.post.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaPostRepository extends JpaRepository<PostEntity, Integer> {
    Page<PostEntity> findAll(Specification<PostEntity> spec, Pageable pageable);

    Page<PostEntity> findAll(Pageable pageable);

    Page<PostEntity> findByStatus(String status, Pageable pageable);

    @Query("SELECT p FROM PostEntity p WHERE p.user.userId = :userId AND p.status = 'ACTIVE'")
    List<PostEntity> findActivePostsByUserId(@Param("userId") Integer userId);
}
