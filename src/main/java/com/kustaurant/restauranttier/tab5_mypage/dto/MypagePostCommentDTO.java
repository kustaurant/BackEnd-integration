package com.kustaurant.restauranttier.tab5_mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypagePostCommentDTO {
    private String postCategory;
    private String postTitle;
    private String postcommentBody;
    private Integer commentlikeCount;
}
