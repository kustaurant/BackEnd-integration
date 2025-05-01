package com.kustaurant.kustaurant.common.user.controller.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypagePostDTO {
    private Integer postId;
    private String postCategory;
    private String postTitle;
    private String postImgUrl;
    private String postBody;
    private Integer likeCount;
    private Integer commentCount;
    private String timeAgo;
}
