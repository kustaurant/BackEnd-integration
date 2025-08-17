package com.kustaurant.kustaurant.post.comment.infrastructure.jpa;

import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentJpaRepository extends JpaRepository<PostCommentEntity, Integer>, JpaSpecificationExecutor<PostCommentEntity> {
    @Query("""
        SELECT pc FROM PostCommentEntity pc
        WHERE pc.userId = :userId AND pc.status = 'ACTIVE'
        ORDER BY pc.createdAt DESC
        """)
    List<PostCommentEntity> findActiveByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    List<PostCommentEntity> findByParentCommentId(Integer parentCommentId);
    
    @Query("""
        SELECT COUNT(pc) FROM PostCommentEntity pc
        WHERE pc.parentCommentId = :parentCommentId AND pc.status = 'ACTIVE'
        """)
    long countActiveRepliesByParentCommentId(@Param("parentCommentId") Integer parentCommentId);

    long countByPostId(Integer postId);

    void deleteByPostId(Integer postId);
}
