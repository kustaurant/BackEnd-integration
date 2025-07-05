package com.kustaurant.kustaurant.post.comment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostCommentDislikeJpaRepository extends JpaRepository<PostCommentDislikeEntity,Integer> {
    Optional<PostCommentDislikeEntity> findByUserIdAndCommentId(Long userId, Integer commentId);

    boolean existsByUserIdAndCommentId(Long userId, Integer commentId);

    int countByCommentId(Integer commentId);

    void deleteByUserIdAndCommentId(Long userId, Integer commentId);
    
    List<PostCommentDislikeEntity> findByCommentId(Integer commentId);
}
