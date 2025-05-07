package com.kustaurant.kustaurant.common.post.service.port;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(Integer id);
    Page<Post> findAll(Pageable pageable);
    Page<Post> findAll(Specification<PostEntity> spec, Pageable pageable);
    Page<Post> findByStatus(String status, Pageable pageable);
    void delete(Post post);
}