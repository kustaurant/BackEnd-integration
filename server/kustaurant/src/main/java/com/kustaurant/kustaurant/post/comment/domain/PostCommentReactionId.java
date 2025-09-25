package com.kustaurant.kustaurant.post.comment.domain;

public record PostCommentReactionId(
        long postCommentId, long userId
) {
}
