package com.kustaurant.kustaurant.v1.mypage.dto;

import lombok.Data;

@Data
public class MypagePostCommentDTO {
    private Integer postId;
    private String postCategory;
    private String postTitle;
    private String postcommentBody;
    private Integer commentlikeCount;
}