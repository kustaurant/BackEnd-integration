package com.kustaurant.kustaurant.user.mypage.infrastructure;

import com.kustaurant.kustaurant.post.infrastructure.PostScrapEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyPostScrapQueryRepository extends Repository<PostScrapEntity, Long> {
    @Query("""
        select distinct ps
        from PostScrapEntity ps
        join fetch ps.post p
        left join fetch p.postPhotoEntityList   ph
        left join fetch p.postCommentList       pc
        where ps.userId = :userId
          and p.status = 'ACTIVE'
        order by p.createdAt desc
    """)
    List<PostScrapEntity> findActiveScrapsByUserId(@Param("userId") Long userId);
}
