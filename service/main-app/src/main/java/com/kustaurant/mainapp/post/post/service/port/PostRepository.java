package com.kustaurant.mainapp.post.post.service.port;

import com.kustaurant.mainapp.post.post.domain.Post;
import com.kustaurant.mainapp.post.post.domain.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> findById(Long id);
    Post save(Post post);
    void delete(Long id);
    void increaseVisitCount(Long postId);
}