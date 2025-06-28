package com.kustaurant.kustaurant.user.mypage.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScrappedPostView {
    private Integer postId;
    private String title;
    private String category;
    private Integer likeCount;
    private String timeAgo;
}
