package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Page<PostEntity> findAll(Specification<PostEntity> spec, Pageable pageable);

    Page<PostEntity> findAll(Pageable pageable);

    List<Post> findAllById(List<Integer> postIds);

    Page<PostEntity> findByStatus(String status, Pageable pageable);

    List<PostEntity> findActivePostsByUserId(Integer userId);

    Optional<PostEntity> findByStatusAndPostId(String status, Integer postId);

    PostEntity save(PostEntity postEntity);

    List<Post> findActiveByUserId(Integer userId);

    Optional<Post> findById(Integer postId);
}