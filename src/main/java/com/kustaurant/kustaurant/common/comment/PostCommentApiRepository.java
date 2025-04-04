package com.kustaurant.kustaurant.common.comment;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentApiRepository extends JpaRepository<PostComment,Integer> {
    List<PostComment> findAll(Specification<PostComment> spec);
}
