package com.kustaurant.kustaurant.comment.service.port;

import com.kustaurant.kustaurant.comment.infrastructure.PostCommentDislike;

import java.util.List;
import java.util.Optional;

public interface PostCommentDislikeRepository {

     void save(PostCommentDislike dislike);
     void deleteById(Integer id);
     List<PostCommentDislike> findByCommentId(Integer commentId);
     Optional<PostCommentDislike> findByUserIdAndCommentId(Long userId, Integer commentId);

    boolean existsByUserIdAndCommentId(Long userId, Integer commentId);

    void deleteByUserIdAndCommentId(Long userId, Integer commentId);

    int countByCommentId(Integer commentId);
}
