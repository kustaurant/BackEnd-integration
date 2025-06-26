package com.kustaurant.kustaurant.user.mypage.infrastructure;

import com.kustaurant.kustaurant.post.infrastructure.PostEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyPostQueryRepository extends Repository<PostEntity, Long> {
    @Query("""
        select distinct p
        from PostEntity p
        left join fetch p.postPhotoEntityList ph
        left join fetch p.postCommentList    pc
        where p.userId = :userId
          and p.status = 'ACTIVE'
        order by p.createdAt desc
    """)
    List<PostEntity> findActivePostsByUserId(@Param("userId") Long userId);
}
