package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.post.comment.infrastructure.PostCommentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyPostCommentQueryRepository extends Repository<PostCommentEntity, Long> {
    @Query("""
        select pc
        from PostCommentEntity pc
        join fetch pc.post p
        where pc.userId = :userId
          and pc.status  = 'ACTIVE'
          and p.status   = 'ACTIVE'
        order by pc.createdAt desc
    """)
    List<PostCommentEntity> findActiveCommentsByUserId(@Param("userId") Long userId);
}
