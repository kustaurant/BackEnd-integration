package com.kustaurant.kustaurant.post.comment.service.port;

import com.kustaurant.kustaurant.post.comment.infrastructure.PostCommentLike;

import java.util.Optional;

public interface PostCommentLikeRepository {
    // Define methods for PostCommentLikeRepository here
    // For example:
     Optional<PostCommentLike> findByUserIdAndCommentId(Long userId, Integer commentId);
     void save(PostCommentLike postCommentLike);
     void deleteByUserIdAndCommentId(Long userId, Integer commentId);
     boolean existsByUserIdAndCommentId(Long userId, Integer commentId);

    int countByCommentId(Integer commentId);
}
