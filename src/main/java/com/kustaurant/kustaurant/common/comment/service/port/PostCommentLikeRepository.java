package com.kustaurant.kustaurant.common.comment.service.port;

import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentLike;

import java.util.Optional;

public interface PostCommentLikeRepository {
    // Define methods for PostCommentLikeRepository here
    // For example:
     Optional<PostCommentLike> findByUserIdAndCommentId(Integer userId, Integer commentId);
     void save(PostCommentLike postCommentLike);
     void deleteByUserIdAndCommentId(Integer userId, Integer commentId);
     boolean existsByUserIdAndCommentId(Integer userId, Integer commentId);
}
