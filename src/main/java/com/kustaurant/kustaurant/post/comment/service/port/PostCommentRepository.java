package com.kustaurant.kustaurant.post.comment.service.port;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository {
    List<PostComment> findActiveByUserId(Long userId);

    List<PostComment> findParentComments(Integer postId);

    Optional<PostComment> findById(Integer comment_id);

    Optional<PostComment> findByIdWithReplies(Integer commentId);
    PostComment save(PostComment comment);

}
