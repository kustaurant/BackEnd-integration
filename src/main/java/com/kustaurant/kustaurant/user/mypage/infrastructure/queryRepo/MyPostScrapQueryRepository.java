package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostScrapEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyPostScrapQueryRepository extends Repository<PostScrapEntity, Long> {
    @Query("""
        select p
        from PostEntity p
             join PostScrapEntity ps on ps.postId = p.postId
        where ps.userId = :userId
          and p.status = 'ACTIVE'
        order by p.createdAt desc
    """)
    List<PostEntity> findScrappedPosts(@Param("userId") Long userId);

    @Query("""
        select p
        from PostEntity p
             join PostScrapEntity ps
               on ps.postId = p.postId
        where ps.userId = :userId
          and p.status = 'ACTIVE'
        order by p.createdAt desc
    """)
    List<PostEntity> findScrappedPostsByUserId(@Param("userId") Long userId);
}
