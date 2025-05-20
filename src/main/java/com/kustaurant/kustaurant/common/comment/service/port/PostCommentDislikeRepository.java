package com.kustaurant.kustaurant.common.comment.service.port;

import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentDislike;

import java.util.List;
import java.util.Optional;

public interface PostCommentDislikeRepository {

     void save(PostCommentDislike dislike);
     void deleteById(Integer id);
     List<PostCommentDislike> findByCommentId(Integer commentId);
     Optional<PostCommentDislike> findByUserIdAndCommentId(Integer userId, Integer commentId);
}
