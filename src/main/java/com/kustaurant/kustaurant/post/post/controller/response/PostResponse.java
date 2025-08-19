package com.kustaurant.kustaurant.post.post.controller.response;

import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostResponse(
        @Schema(description = "게시글 ID")
        Integer postId,
        @Schema(description = "게시글 카테고리")
        PostCategory category,
        @Schema(description = "게시글 제목")
        String title,
        @Schema(description = "게시글 내용")
        String body,
        @Schema(description = "게시글에 등록된 이미지 url주소들")
        List<String> photoUrls
) {
    public static PostResponse from(Post p, List<String> photoUrls) {
        return new PostResponse(
                p.getId(),
                p.getCategory(),
                p.getTitle(),
                p.getBody(),
                photoUrls
        );
    }

}
