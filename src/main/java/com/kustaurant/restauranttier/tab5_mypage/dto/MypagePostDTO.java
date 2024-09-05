package com.kustaurant.restauranttier.tab5_mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypagePostDTO {
    private String postCategory;
    private String postTitle;
    private String postBody;
    private Integer likeCount;
    private Integer commentCount;
    private String timeAgo;
}
