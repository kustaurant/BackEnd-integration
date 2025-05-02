package com.kustaurant.kustaurant.common.user.controller.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MypageWebPostScrabDTO {
    private Long postId;
    private String postTitle;
    private String postCategory;
    private String userNickname;
    private String timeAgo;
}
