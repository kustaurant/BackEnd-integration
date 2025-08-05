package com.kustaurant.kustaurant.user.mypage.controller.response.web;

import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScrappedPostView {
    private Integer postId;
    private String title;
    private PostCategory category;
    private Integer likeCount;
    private String timeAgo;
}
