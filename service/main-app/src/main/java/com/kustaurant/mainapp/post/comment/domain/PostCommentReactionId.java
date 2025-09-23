package com.kustaurant.mainapp.post.comment.domain;

public record PostCommentReactionId(
        long postCommentId, long userId
) {
}
