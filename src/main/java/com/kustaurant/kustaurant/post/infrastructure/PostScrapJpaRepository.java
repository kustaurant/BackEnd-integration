package com.kustaurant.kustaurant.post.infrastructure;

import com.kustaurant.kustaurant.user.infrastructure.UserEntity;
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

    boolean existsByUser_UserIdAndPost_PostId(Integer userId, Integer postId);


    void deleteByPost_PostId(Integer postId);

    Optional<PostScrapEntity> findByUser_UserIdAndPost_PostId(Integer userId, Integer postId);

    @Query("""
        select ps
          from PostScrapEntity ps
          join fetch ps.post p
         where ps.user.userId = :userId
           and p.status     = 'ACTIVE'
         order by ps.createdAt desc
    """)
    List<PostScrapEntity> findWithPostByUserId(@Param("userId") Integer userId);
}
