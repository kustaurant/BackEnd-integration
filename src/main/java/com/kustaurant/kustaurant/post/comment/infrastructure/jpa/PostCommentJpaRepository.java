package com.kustaurant.kustaurant.post.comment.infrastructure.jpa;

import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostCommentJpaRepository extends JpaRepository<PostCommentEntity, Integer>, JpaSpecificationExecutor<PostCommentEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from PostCommentEntity c where c.commentId = :id")
    Optional<PostCommentEntity> findByIdForUpdate(@Param("id") Integer id);

    List<PostCommentEntity> findByParentCommentId(Integer parentCommentId);
    
    @Query("""
        SELECT COUNT(pc) FROM PostCommentEntity pc
        WHERE pc.parentCommentId = :parentCommentId AND pc.status = 'ACTIVE'
        """)
    long countActiveRepliesByParentCommentId(@Param("parentCommentId") Integer parentCommentId);

    long countByPostId(Long postId);

    void deleteByPostId(Long postId);
}
