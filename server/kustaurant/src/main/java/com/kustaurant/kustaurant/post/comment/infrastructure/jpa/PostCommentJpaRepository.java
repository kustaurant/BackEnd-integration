package com.kustaurant.kustaurant.post.comment.infrastructure.jpa;

import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostCommentJpaRepository extends JpaRepository<PostCommentEntity, Long> {

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<PostCommentEntity> findById(Long aLong);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from PostCommentEntity c where c.postCommentId = :id")
    Optional<PostCommentEntity> findByIdForUpdate(@Param("id") Long id);

    @Query("""
        SELECT COUNT(pc) FROM PostCommentEntity pc
        WHERE pc.parentCommentId = :parentCommentId AND pc.status = 'ACTIVE'
        """)
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    long countActiveRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    long countByPostId(Long postId);

    void deleteByPostId(Long postId);
}
