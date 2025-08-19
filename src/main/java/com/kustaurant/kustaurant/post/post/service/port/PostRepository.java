package com.kustaurant.kustaurant.post.post.service.port;

import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> findById(Integer id);
    Post save(Post post);
    void delete(Integer id);
    List<Post> findAllById(List<Integer> ids);
    void increaseVisitCount(Integer postId);
    List<Post> findByUserId(Long userId);
}