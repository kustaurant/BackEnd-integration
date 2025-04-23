package com.kustaurant.kustaurant.common.comment.service.port;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;

import java.util.List;

public interface PostCommentRepository {
    List<PostComment> findActiveByUserId(Integer userId);
}
