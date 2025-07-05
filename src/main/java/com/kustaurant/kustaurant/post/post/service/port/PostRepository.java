package com.kustaurant.kustaurant.post.post.service.port;

import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<PostEntity> findActivePostsByUserId(Long userId);

    Post findByStatusAndPostId(ContentStatus status, Integer postId);

    PostEntity save(PostEntity postEntity);
    Post save(Post post);
    Optional<Post> findById(Integer id);
    Optional<Post> findByIdWithComments(Integer id);
    Page<Post> findAll(Pageable pageable);

    List<Post> findAllById(List<Integer> ids);

    Page<Post> findByStatus(ContentStatus status, Pageable pageable);

    void increaseVisitCount(Integer postId);
    void delete(Post post);

    List<Post> findActiveByUserId(Long userId);
    
    // 서비스 계층에서 직접 파라미터를 받는 메서드들
    Page<Post> findByStatusAndCategory(ContentStatus status, String category, Pageable pageable);
    Page<Post> findByStatusAndPopularCount(ContentStatus status, int minLikeCount, Pageable pageable);
    Page<Post> findByStatusAndCategoryAndPopularCount(ContentStatus status, String category, int minLikeCount, Pageable pageable);
    Page<Post> findByStatusAndSearchKeyword(ContentStatus status, String keyword, String category, int minLikeCount, Pageable pageable);
}