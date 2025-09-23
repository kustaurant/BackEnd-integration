package com.kustaurant.mainapp.post.comment.service.port;

import com.kustaurant.mainapp.post.comment.domain.PostComment;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository {

    Optional<PostComment> findById(Long id);
    Optional<PostComment> findByIdForUpdate(Long id);

    PostComment save(PostComment comment);

    
    long countActiveRepliesByParentCommentId(Long parentCommentId);
    long countVisibleRepliesByPostId(Long postId); // 댓글,대댓글 status = active인것들 모두 count

    void delete(PostComment comment);
    void deleteByPostId(Long postId);
}
