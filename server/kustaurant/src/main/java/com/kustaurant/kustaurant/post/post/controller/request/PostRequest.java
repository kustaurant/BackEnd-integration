package com.kustaurant.kustaurant.post.post.controller.request;

import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostRequest(
        @NotBlank @Size(min = 1, max = 100)
        @Schema(description = "제목(1자 이상, 100자 이하)", example = "업데이트된 맛집 추천")
        String title,

        @NotNull
        @Schema(description = "카테코리(FREE,COLUMN,SUGGESTION중 하나)", example = "FREE")
        PostCategory category,

        @NotBlank @Size(min = 1, max = 10_000)
        @Schema(description = "본문(1자 이상, 10,000자 이하)", example = "건대 맛집을 다녀왔습니다.")
        String content
) {
        @AssertTrue(message = "category=ALL 은 생성 시 사용할 수 없습니다.")
        public boolean validCategoryForCreate() {
                return category != PostCategory.ALL;
        }
}