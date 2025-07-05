package com.kustaurant.kustaurant.post.comment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentJpaRepository extends JpaRepository<PostCommentEntity, Integer>, JpaSpecificationExecutor<PostCommentEntity> {
    @Query("""
        SELECT pc FROM PostCommentEntity pc
        WHERE pc.userId = :userId AND pc.status = 'ACTIVE'
        ORDER BY pc.createdAt DESC""")
    List<PostCommentEntity> findActiveByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    List<PostCommentEntity> findByPostId(Integer postId);
    
    List<PostCommentEntity> findByPostIdAndStatus(Integer postId, String status);
    
    List<PostCommentEntity> findByParentCommentId(Integer parentCommentId);
}
