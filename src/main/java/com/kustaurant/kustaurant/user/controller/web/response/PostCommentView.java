package com.kustaurant.kustaurant.user.controller.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCommentView {
    private final Integer commentId;
    private final String commentBody;
    private final Integer likeCount;
    private final String timeAgo;
    private final String postTitle;
    private final Integer postId;
}
