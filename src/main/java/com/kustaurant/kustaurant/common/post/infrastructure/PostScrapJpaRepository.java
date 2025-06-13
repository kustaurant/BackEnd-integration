package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostScrapJpaRepository extends JpaRepository<PostScrapEntity, Integer> {
    @Query("""
        SELECT ps FROM PostScrapEntity ps
        WHERE ps.user.id = :userId
        ORDER BY ps.createdAt DESC""")
    List<PostScrapEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);


    @Query("SELECT ps FROM PostScrapEntity ps WHERE ps.user.userId = :userId AND ps.post.status = 'ACTIVE'")
    List<PostScrapEntity> findActiveScrappedPostsByUserId(@Param("userId") Integer userId);

    boolean existsByUser_UserIdAndPost_PostId(Integer userId, Integer postId);

    Integer user(UserEntity user);

    void deleteByPost_PostId(Integer postId);

    Optional<PostScrapEntity> findByUser_UserIdAndPost_PostId(Integer userId, Integer postId);
}
