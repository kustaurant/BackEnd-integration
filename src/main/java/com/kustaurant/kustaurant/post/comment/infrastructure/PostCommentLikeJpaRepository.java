package com.kustaurant.kustaurant.post.comment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentLikeJpaRepository extends JpaRepository<PostCommentLikeEntity, Integer> {
    Optional<PostCommentLikeEntity> findByUserIdAndPostComment_CommentId(Long userId, Integer commentId);

    boolean existsByUserIdAndPostComment_CommentId(Long userId, Integer commentId);

    int countByPostComment_CommentId(Integer commentId);

    void deleteByUserIdAndPostComment_CommentId(Long userId, Integer commentId);
}
