package com.kustaurant.kustaurant.comment.service.port;

import com.kustaurant.kustaurant.comment.infrastructure.PostCommentDislike;

import java.util.List;
import java.util.Optional;

public interface PostCommentDislikeRepository {

     void save(PostCommentDislike dislike);
     void deleteById(Integer id);
     List<PostCommentDislike> findByCommentId(Integer commentId);
     Optional<PostCommentDislike> findByUserIdAndCommentId(Integer userId, Integer commentId);

    boolean existsByUserIdAndCommentId(Integer userId, Integer commentId);

    void deleteByUserIdAndCommentId(Integer userId, Integer commentId);

    int countByCommentId(Integer commentId);
}
