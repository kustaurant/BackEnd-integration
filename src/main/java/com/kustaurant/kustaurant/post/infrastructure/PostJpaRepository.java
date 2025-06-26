package com.kustaurant.kustaurant.post.infrastructure;

import com.kustaurant.kustaurant.post.enums.ContentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<PostEntity, Integer> {
    Page<PostEntity> findAll(Specification<PostEntity> spec, Pageable pageable);

    Page<PostEntity> findAll(Pageable pageable);

    Page<PostEntity> findByStatus(ContentStatus status, Pageable pageable);

    @Query("SELECT p FROM PostEntity p WHERE p.user.userId = :userId AND p.status = 'ACTIVE'")
    List<PostEntity> findActivePostsByUserId(@Param("userId") Long userId);

    Optional<PostEntity> findByStatusAndPostId(String status, Integer postId);

    @Query("""
        SELECT p FROM PostEntity p
        WHERE p.user.userId = :userId AND p.status = 'ACTIVE'
        ORDER BY p.createdAt DESC""")
    List<PostEntity> findActiveByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);

}
