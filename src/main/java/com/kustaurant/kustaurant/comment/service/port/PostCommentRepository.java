package com.kustaurant.kustaurant.comment.service.port;

import com.kustaurant.kustaurant.comment.domain.PostComment;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository {
    List<PostComment> findActiveByUserId(Integer userId);

    List<PostComment> findParentComments(Integer postId);

    Optional<PostComment> findById(Integer comment_id);

    Optional<PostComment> findByIdWithReplies(Integer commentId);
    PostComment save(PostComment comment);

}
