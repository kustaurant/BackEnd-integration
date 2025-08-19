package com.kustaurant.kustaurant.post.comment.service.port;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository {

    Optional<PostComment> findById(Integer comment_id);

    PostComment save(PostComment comment);
    
    List<PostComment> findByParentCommentId(Integer parentCommentId);
    
    long countActiveRepliesByParentCommentId(Integer parentCommentId);
    long countActiveRepliesByPostId(Integer postId);
    void delete(PostComment comment);
    List<PostComment> saveAll(List<PostComment> comments);
    void deleteByPostId(Integer postId);
}
