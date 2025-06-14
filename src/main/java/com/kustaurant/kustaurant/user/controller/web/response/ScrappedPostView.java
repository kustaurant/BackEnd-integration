package com.kustaurant.kustaurant.user.controller.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScrappedPostView {
    private Integer postId;
    private String title;
    private String category;
    private String writerNickname;
    private Integer likeCount;
    private String timeAgo;
}
