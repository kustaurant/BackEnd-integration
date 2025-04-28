package com.kustaurant.kustaurant.common.post.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostScrapJpaRepository extends JpaRepository<PostEntity, Integer> {
    @Query("""
        SELECT ps FROM PostScrapEntity ps
        WHERE ps.user.id = :userId
        ORDER BY ps.createdAt DESC""")
    List<PostScrapEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);
}
