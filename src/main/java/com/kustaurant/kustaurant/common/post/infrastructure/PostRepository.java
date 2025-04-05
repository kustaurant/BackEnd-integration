package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Page<PostEntity> findAll(Specification<PostEntity> spec, Pageable pageable);

    Page<PostEntity> findAll(Pageable pageable);

    Page<PostEntity> findByStatus(String status, Pageable pageable);

    List<PostEntity> findActivePostsByUserId(Integer userId);

    Optional<PostEntity> findByStatusAndPostId(String status, Integer postId);

    PostEntity save(PostEntity postEntity);

    Optional<Post> findById(Integer postId);
}