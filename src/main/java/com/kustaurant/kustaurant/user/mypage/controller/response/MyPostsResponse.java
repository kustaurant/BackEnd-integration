package com.kustaurant.kustaurant.user.mypage.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record MyPostsResponse (
        Integer postId,
        String postCategory,
        String postTitle,
        String postImgUrl,
        String postBody,
        Integer likeCount,
        Integer commentCount,
        String timeAgo
){
}
