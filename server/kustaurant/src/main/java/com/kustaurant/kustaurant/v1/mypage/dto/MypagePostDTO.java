package com.kustaurant.kustaurant.v1.mypage.dto;

import lombok.Data;

@Data
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
