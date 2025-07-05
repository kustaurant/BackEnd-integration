package com.kustaurant.kustaurant.post.comment.infrastructure;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentApiRepository extends JpaRepository<PostCommentEntity,Integer> {
    List<PostCommentEntity> findAll(Specification<PostCommentEntity> spec);
    
    List<PostCommentEntity> findByParentCommentId(Integer parentCommentId);
}
