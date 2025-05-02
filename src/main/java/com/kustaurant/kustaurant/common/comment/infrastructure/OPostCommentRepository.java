package com.kustaurant.kustaurant.common.comment.infrastructure;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OPostCommentRepository extends JpaRepository<PostCommentEntity,Integer> {
    List<PostCommentEntity> findAll(Specification<PostCommentEntity> spec);

    @Query("SELECT pc FROM PostCommentEntity pc WHERE pc.user.userId = :userId AND pc.status = 'ACTIVE'")
    List<PostCommentEntity> findActiveCommentedPostsByUserId(@Param("userId") Integer userId);
}
