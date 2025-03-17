package com.kustaurant.kustaurant.common.user.domain;

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
}
