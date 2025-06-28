package com.kustaurant.kustaurant.post.post.service.port;

import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<PostEntity> findActivePostsByUserId(Long userId);

    Optional<PostEntity> findByStatusAndPostId(String status, Integer postId);

    PostEntity save(PostEntity postEntity);
    Post save(Post post);
    Optional<Post> findById(Integer id);
    Optional<Post> findByIdWithComments(Integer id);
    Page<Post> findAll(Pageable pageable);
    Page<Post> findAll(Specification<PostEntity> spec, Pageable pageable);

    List<Post> findAllById(List<Integer> ids);

    Page<Post> findByStatus(ContentStatus status, Pageable pageable);

    void increaseVisitCount(Integer postId);
    void delete(Post post);

    List<Post> findActiveByUserId(Long userId);
}