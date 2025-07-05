package com.kustaurant.kustaurant.post.comment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentLikeJpaRepository extends JpaRepository<PostCommentLikeEntity, Integer> {
    Optional<PostCommentLikeEntity> findByUserIdAndCommentId(Long userId, Integer commentId);

    boolean existsByUserIdAndCommentId(Long userId, Integer commentId);

    int countByCommentId(Integer commentId);

    void deleteByUserIdAndCommentId(Long userId, Integer commentId);
}
