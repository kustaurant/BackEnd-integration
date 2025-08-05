package com.kustaurant.kustaurant.post.post.service.port;

import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> findById(Integer id);
    Post findByStatusAndPostId(PostStatus status, Integer postId);
    Post save(Post post);
    void delete(Integer id);
    List<Post> findAllById(List<Integer> ids);
    void increaseVisitCount(Integer postId);
    List<Post> findByUserId(Long userId);
    
    // 서비스 계층에서 직접 파라미터를 받는 메서드들
    Page<Post> findByStatusAndCategory(PostStatus status, String category, Pageable pageable);
    Page<Post> findByStatusAndPopularCount(PostStatus status, int minLikeCount, Pageable pageable);
    Page<Post> findByStatusAndCategoryAndPopularCount(PostStatus status, String category, int minLikeCount, Pageable pageable);
    Page<Post> findByStatusAndSearchKeyword(PostStatus status, String keyword, String category, int minLikeCount, Pageable pageable);
}