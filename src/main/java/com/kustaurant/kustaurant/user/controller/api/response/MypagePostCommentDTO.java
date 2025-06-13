package com.kustaurant.kustaurant.user.controller.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypagePostCommentDTO {
    private Integer postId;
    private String postCategory;
    private String postTitle;
    private String postcommentBody;
    private Integer commentlikeCount;
    private String timeAgo;
}
