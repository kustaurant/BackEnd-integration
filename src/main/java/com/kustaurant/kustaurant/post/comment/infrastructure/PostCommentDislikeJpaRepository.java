package com.kustaurant.kustaurant.post.comment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentDislikeJpaRepository extends JpaRepository<PostCommentDislikeEntity,Integer> {
    Optional<PostCommentDislikeEntity> findByUserIdAndPostComment_CommentId(Long userId, Integer commentId);

    boolean existsByUserIdAndPostComment_CommentId(Long userId, Integer commentId);

    int countByPostComment_CommentId(Integer commentId);

    void deleteByUserIdAndPostComment_CommentId(Long userId, Integer commentId);
}
