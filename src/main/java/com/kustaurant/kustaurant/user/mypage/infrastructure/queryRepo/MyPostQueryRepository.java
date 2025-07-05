package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyPostQueryRepository extends Repository<PostEntity, Long> {
    @Query("""
        select p
        from PostEntity p
        where p.userId = :userId
          and p.status = 'ACTIVE'
        order by p.createdAt desc
    """)
    List<PostEntity> findActivePostsByUserId(@Param("userId") Long userId);

}
