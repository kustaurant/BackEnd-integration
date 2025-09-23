package com.kustaurant.mainapp.post.community.controller.request;

import com.kustaurant.mainapp.common.enums.SortOption;
import com.kustaurant.mainapp.post.post.domain.enums.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record PostListRequest(
        @Schema(description = "게시판 종류", example = "FREE", defaultValue = "ALL")
        PostCategory category,
        @Schema(description = "페이지 인덱스(0부터 시작)", example = "0", defaultValue = "0")
        @Min(0) Integer page,
        @Schema(description = "정렬 방식 (POPULARITY, LATEST)", example = "POPULARITY", defaultValue = "LATEST")
        SortOption sort
) {
    public PostListRequest {
        if (category == null) category = PostCategory.ALL;
        if (sort == null) sort = SortOption.LATEST;
        if (page == null || page < 0) page = 0;
    }
}
